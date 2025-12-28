package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.client.renderer.SmokeRenderManager
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.particle.SmokeGrenadeParticle
import club.pisquad.minecraft.csgrenades.registery.ModDamageType
import club.pisquad.minecraft.csgrenades.registery.ModItems
import club.pisquad.minecraft.csgrenades.registery.ModSerializers
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.Registries
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.fml.ModList
import java.time.Duration
import java.time.Instant
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

class SmokeGrenadeEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.FLASH_BANG) {

    private var lastPos: Vec3i = Vec3i(0, 0, 0)
    private val particles = mutableMapOf<Vec3i, List<SmokeGrenadeParticle>>()
    private var explosionTime: Instant? = null
    private val spreadBlocksCache: MutableList<@Serializable BlockPos> = mutableListOf()

    // For freezing rotation after explosion
    private var hasSavedFinalRotation = false
    private var finalXRot = 0f
    private var finalYRot = 0f
    private var finalZRot = 0f

    override fun getDefaultItem(): Item {
        return ModItems.SMOKE_GRENADE_ITEM.get()
    }

    companion object {
        val spreadBlocksAccessor: EntityDataAccessor<List<@Serializable BlockPos>> = SynchedEntityData.defineId(
            SmokeGrenadeEntity::class.java,
            ModSerializers.blockPosListEntityDataSerializer
        )
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(spreadBlocksAccessor, listOf())
    }

    fun registerParticle(particle: SmokeGrenadeParticle) {
        val pos = particle.pos.toVec3i()
        if (this.particles.containsKey(pos)) {
            this.particles[pos] = this.particles[pos]!!.plus(particle)
        } else {
            this.particles[pos] = listOf(particle)
        }
    }

    fun clearSmokeWithinRange(position: Vec3, range: Double, printHeader: Boolean) {
        if (printHeader) {
            println("CS-GRENADES DEBUG: Clearing smoke at pos: $position with range: $range. Checking ALL particles.")
            val totalParticles = this.particles.values.sumOf { it.size }
            println("CS-GRENADES DEBUG: Total particles in map: $totalParticles")
            if (totalParticles == 0) {
                println("CS-GRENADES DEBUG: Cleared 0 particles because the map is empty.")
                return // Nothing to do
            }
        }

        var clearedCount = 0
        var checkedCount = 0 // To avoid spamming logs

        // Iterate over all lists of particles in the map
        this.particles.values.forEach { particleList ->
            // Iterate over each particle in the list
            particleList.forEach { particle ->
                // New Debugging: Print first few particle positions
                if (printHeader && checkedCount < 5) {
                    val dist = particle.pos.distanceTo(position)
                    println("CS-GRENADES DEBUG:   - Checking particle at ${particle.pos}, distance to bullet: $dist")
                }
                checkedCount++

                // Directly check the distance between the bullet and the particle
                if (particle.pos.distanceToSqr(position) < range * range) { // Use distanceToSqr for performance
                    particle.opacityTime = getRegenerationTime(particle.pos.distanceTo(position), range)
                    clearedCount++
                }
            }
        }

        if (clearedCount > 0) {
            println("CS-GRENADES DEBUG: Cleared $clearedCount particles at interpolated position.")
        }
    }

    private fun getRegenerationTime(distance: Double, radius: Double): Int {
        return ModConfig.SmokeGrenade.TIME_BEFORE_REGENERATE.get().millToTick().toInt() + linearInterpolate(
            ModConfig.SmokeGrenade.REGENERATION_TIME.get().millToTick().toDouble(),
            0.0,
            distance / radius
        ).toInt()
    }

    override fun tick() {
        if (this.entityData.get(isExplodedAccessor)) {
            // Forcefully freeze rotation and position
            if (this.level().isClientSide) {
                if (!hasSavedFinalRotation) {
                    // Save the final rotation the first tick it's exploded
                    finalXRot = this.xRot
                    finalYRot = this.yRot
                    finalZRot = this.zRot
                    hasSavedFinalRotation = true
                }
                // On every subsequent tick, force the rotation back to the saved values
                this.xRot = finalXRot
                this.yRot = finalYRot
                this.zRot = finalZRot
                this.xRotO = finalXRot
                this.yRotO = finalYRot
                this.zRotO = finalZRot
            }

            // Smoke-specific logic still needs to run
            if (this.level() is ServerLevel) {
                if (this.explosionTime != null && Duration.between(
                        this.explosionTime,
                        Instant.now()
                    ) > Duration.ofMillis(
                        ModConfig.SmokeGrenade.SMOKE_LIFETIME.get().toLong()
                    )
                ) {
                    this.kill()
                }
                extinguishNearbyFires()
            }
            if (this.level().isClientSide) {
                this.disperseSmokeByProjectiles()
            }
            return // IMPORTANT: Do not execute any more tick logic (including super.tick())
        }

        super.tick() // Only run physics tick if not exploded

        if (this.entityData.get(isLandedAccessor)) {
            if (this.position() == Vec3(this.xOld, this.yOld, this.zOld)) {
                this.tickCount++
            } else {
                tickCount = 0
            }
            if (this.tickCount > ModConfig.SmokeGrenade.FUSE_TIME_AFTER_LANDING.get().millToTick()
                && this.explosionTime == null
            ) {
                if (this.level().isClientSide) {
                    this.clientRenderEffect()
                } else {
                    this.entityData.set(spreadBlocksAccessor, calculateSpreadBlocks())
                    this.item = net.minecraft.world.item.ItemStack.EMPTY
                }
                this.entityData.set(isExplodedAccessor, true)
                this.explosionTime = Instant.now()
            }
        }
    }

    private fun disperseSmokeByProjectiles() {
        val spreadBlocks = this.getSpreadBlocks()
        if (spreadBlocks.isEmpty()) {
            return
        }

        // --- Vanilla Arrow Logic ---
        // A large bounding box to catch any arrows that might be nearby. The swept BB check is more precise.
        val searchBB = this.boundingBox.inflate(64.0)
        val nearbyArrows = this.level().getEntitiesOfClass(
            net.minecraft.world.entity.projectile.AbstractArrow::class.java,
            searchBB
        ) { arrow -> arrow.deltaMovement.lengthSqr() > 0.01 } // Only consider moving arrows

        val smokeRadius = ModConfig.SmokeGrenade.SMOKE_RADIUS.get().toDouble()
        val smokeFallingHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get().toDouble()
        val smokeCloudBoundingBox =
            AABB(this.blockPosition()).inflate(smokeRadius).expandTowards(0.0, -smokeFallingHeight, 0.0)

        nearbyArrows.forEach { arrow ->
            // Use a "swept" bounding box to detect fast-moving entities that pass through the cloud in a single tick.
            val delta = arrow.deltaMovement
            val currentBB = arrow.boundingBox
            val oldBB = currentBB.move(-delta.x, -delta.y, -delta.z)
            val sweptBB = currentBB.minmax(oldBB)

            if (smokeCloudBoundingBox.intersects(sweptBB)) {
                println("CS-GRENADES DEBUG: Swept BB intersection SUCCESS for Arrow.")
                // Interpolate position to prevent tunneling
                val posNow = arrow.position()
                val posOld = posNow.subtract(delta)
                val steps = (delta.length() / 0.5).toInt().coerceAtLeast(1)
                    .coerceAtMost(30) // Check every 50cm, with a higher cap
                for (i in 0..steps) {
                    val interpolatedPos = posOld.lerp(posNow, i.toDouble() / steps)
                    this.clearSmokeWithinRange(interpolatedPos, ModConfig.SmokeGrenade.ARROW_CLEAR_RANGE.get(), i == 0)
                }
            }
        }

        // --- TACZ BULLET COMPATIBILITY ---
        if (this.level().isClientSide && ModList.get().isLoaded("tacz")) {
            val clientLevel = this.level() as? net.minecraft.client.multiplayer.ClientLevel ?: return
            val allRenderEntities = clientLevel.entitiesForRendering()

            if (allRenderEntities.none()) {
                return
            }

            val smokeCenter = this.position()
            val smokeRadius = ModConfig.SmokeGrenade.SMOKE_RADIUS.get().toDouble()
            val smokeFallingHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get().toDouble()
            val smokeCloudBoundingBox =
                AABB(BlockPos.containing(smokeCenter)).inflate(smokeRadius).expandTowards(0.0, -smokeFallingHeight, 0.0)

            allRenderEntities.forEach { entity ->
                // Use a "swept" bounding box to detect fast-moving entities that pass through the cloud in a single tick.
                val delta = entity.deltaMovement
                // If the entity hasn't moved, we don't need to check it.
                if (delta.lengthSqr() == 0.0) return@forEach

                val currentBB = entity.boundingBox
                val oldBB = currentBB.move(-delta.x, -delta.y, -delta.z)
                val sweptBB = currentBB.minmax(oldBB)

                // Check if the swept path intersects the smoke cloud
                if (smokeCloudBoundingBox.intersects(sweptBB)) {
                    if (entity::class.java.name == "com.tacz.guns.entity.EntityKineticBullet") {
                        println("CS-GRENADES DEBUG: Swept BB intersection SUCCESS for Kinetic Bullet.")

                        val posNow = entity.position()
                        val finalClearRange = ModConfig.SmokeGrenade.BULLET_CLEAR_RANGE.get()
                        val posOld = posNow.subtract(delta)

                        // Interpolation logic remains the same
                        val steps = (delta.length() / 0.5).toInt().coerceAtLeast(1).coerceAtMost(30)
                        for (i in 0..steps) {
                            val interpolatedPos = posOld.lerp(posNow, i.toDouble() / steps)
                            this.clearSmokeWithinRange(interpolatedPos, finalClearRange, i == 0)
                        }
                    }
                }
            }
        }
    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        lastPos = this.position().toVec3i()
    }

    override fun onHitBlock(result: BlockHitResult) {
        // If the smoke hit the ground with in any incendiary's range, it will emit right away
        super.onHitBlock(result)
        if (result.direction == Direction.UP) {
            if (this.extinguishNearbyFires() > 0) {
                this.entityData.set(isLandedAccessor, true)
                if (this.level() is ServerLevel && result.isInside) {
                    this.setPos(Vec3(this.position().x, result.blockPos.y + 1.0, this.position().z))
                }
            }
        }
    }

    private fun extinguishNearbyFires(): Int {
        val extinguishedFires: List<AbstractFireGrenade>
        val smokeRadius = ModConfig.SmokeGrenade.SMOKE_RADIUS.get()
        val smokeFallingHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get()
        if (this.entityData.get(isExplodedAccessor)) {
            val bb = AABB(this.blockPosition()).inflate(
                smokeRadius.toDouble(),
                smokeFallingHeight.toDouble(),
                smokeRadius.toDouble()
            )

            extinguishedFires = this.level().getEntitiesOfClass(
                AbstractFireGrenade::class.java,
                bb
            ) {
                it.entityData.get(isExplodedAccessor) && canDistinguishFire(it.position())
            }
        } else {
            val bb = AABB(this.blockPosition()).inflate(ModConfig.FireGrenade.FIRE_RANGE.get().toDouble())
            extinguishedFires = this.level().getEntitiesOfClass(
                AbstractFireGrenade::class.java,
                bb
            ) {
                it.entityData.get(isExplodedAccessor) && it.getSpreadBlocks()
                    .any { pos -> pos.above().center.distanceToSqr(this.position()) < 2 }
            }
        }

        if (this.level() is ServerLevel) {
            extinguishedFires.forEach {
                it.extinguish()
            }
        }
        return extinguishedFires.size
    }

    private fun clientRenderEffect() {
        val player = Minecraft.getInstance().player ?: return
        val distance = this.position().subtract(player.position()).length()


        // Sounds
        val soundManager = Minecraft.getInstance().soundManager
        val soundEvent =
            if (distance > 30) ModSoundEvents.SMOKE_EXPLODE_DISTANT.get() else ModSoundEvents.SMOKE_EMIT.get()
        val soundType =
            if (distance > 30) SoundTypes.SMOKE_GRENADE_EXPLODE_DISTANT else SoundTypes.SMOKE_GRENADE_EMIT

        val soundInstance = EntityBoundSoundInstance(
            soundEvent,
            SoundSource.AMBIENT,
            SoundUtils.getVolumeFromDistance(distance, soundType).toFloat(),
            1f,
            this,
            0
        )
        soundManager.play(soundInstance)

        // Particles
        SmokeRenderManager.render(
            Minecraft.getInstance().particleEngine,
            this.position(),
            this,
        )
    }

    private fun calculateSpreadBlocks(): List<BlockPos> {
        // --- Smart Origin Sanitization ---
        // Determines the actual starting point for smoke generation.
        // If the grenade's block position is inside a solid block (e.g., stair crevice),
        // it finds the adjacent air block the grenade is actually "poking" into.
        var validatedOrigin = this.blockPosition()
        val originalBlockState = this.level().getBlockState(validatedOrigin)

        if (!originalBlockState.getCollisionShape(this.level(), validatedOrigin).isEmpty) {
            // Grenade is in a solid block. Try to find the "opening" using precise position.
            val precisePos = this.position() // Precise float position of the entity
            val blockCenter = Vec3.atCenterOf(validatedOrigin) // Center of the solid block

            // Vector from block center to precise entity position. This points towards the "opening".
            val escapeVector = precisePos.subtract(blockCenter)

            // Find the dominant axis of this escape vector to determine the direction of the opening.
            val bestDirection = Direction.getNearest(escapeVector.x, escapeVector.y, escapeVector.z)

            val potentialOrigin = validatedOrigin.relative(bestDirection, 1)
            if (this.level().getBlockState(potentialOrigin).getCollisionShape(this.level(), potentialOrigin).isEmpty) {
                validatedOrigin = potentialOrigin
            } else {
                // The "smart" direction is also blocked. Fallback: search for first empty neighbor.
                val firstEmptyNeighbor = listOf(
                    validatedOrigin.above(), validatedOrigin.below(),
                    validatedOrigin.north(), validatedOrigin.south(),
                    validatedOrigin.east(), validatedOrigin.west()
                ).firstOrNull { pos -> this.level().getBlockState(pos).getCollisionShape(this.level(), pos).isEmpty }

                validatedOrigin = firstEmptyNeighbor ?: validatedOrigin.above(2) // As a last resort, go 2 blocks up.
            }
        }

        // 1. Calculate the initial "ideal" smoke cloud as before.
        val initialSmoke: Set<BlockPos> = SmokeGrenadeSpreadBlockCalculator(
            5, 1500, 2, validatedOrigin // Use the sanitized origin
        ).calculate(this.level())

        if (initialSmoke.isEmpty()) return emptyList()

        val maxFallHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get()
        val smokeColumns = initialSmoke.groupBy { Pair(it.x, it.z) }
        val totalColumnCount = smokeColumns.size.coerceAtLeast(1) // Avoid division by zero

        // --- Calculation Pass: Determine raw fall distance and support for each column ---
        val columnFallInfo = mutableMapOf<Pair<Int, Int>, Int>() // Stores raw fall distance
        var landingColumnCount = 0

        for ((key, columnBlocks) in smokeColumns) {
            if (columnBlocks.isEmpty()) continue
            val lowestBlock = columnBlocks.minByOrNull { it.y } ?: continue

            var fallDistance = 0
            var hitGround = false
            var currentPos = lowestBlock
            for (i in 0 until maxFallHeight) {
                if (this.level().getBlockState(currentPos.below())
                        .getCollisionShape(this.level(), currentPos.below()).isEmpty
                ) {
                    fallDistance++
                    currentPos = currentPos.below()
                } else {
                    hitGround = true
                    break
                }
            }
            columnFallInfo[key] = fallDistance
            if (hitGround) {
                landingColumnCount++
            }
        }

        // --- Decision Pass: Decide between spherical and slumping shape ---
        val supportThreshold = 0.3 // 30%
        val landingPercentage = landingColumnCount.toDouble() / totalColumnCount

        if (landingPercentage < supportThreshold) {
            // Not enough support. Stay spherical.
            return initialSmoke.toList()
        }

        // --- Smoothing and Application Pass: Apply center-weighted slumping ---
        val finalSmoke = mutableSetOf<BlockPos>()
        val grenadePos = this.blockPosition()
        val centerKey = Pair(grenadePos.x, grenadePos.z)
        val centerFallDistance = columnFallInfo[centerKey] ?: 0 // Raw fall distance of the center

        // Find max distance from center for normalization
        var maxDist = 0.0
        for (key in smokeColumns.keys) {
            val dist = kotlin.math.sqrt(
                (key.first - centerKey.first).toDouble().pow(2.0) + (key.second - centerKey.second).toDouble().pow(2.0)
            )
            if (dist > maxDist) maxDist = dist
        }
        maxDist = maxDist.coerceAtLeast(1.0) // Avoid division by zero

        for ((key, columnBlocks) in smokeColumns) {
            val rawFallDistance = columnFallInfo[key] ?: 0

            val distFromCenter = kotlin.math.sqrt(
                (key.first - centerKey.first).toDouble().pow(2.0) + (key.second - centerKey.second).toDouble().pow(2.0)
            )

            // Weight is high (1.0) at the center, and low (0.0) at the max distance.
            val weight = (1.0 - (distFromCenter / maxDist)).coerceIn(0.0, 1.0)

            // The final fall distance is interpolated between the column's raw distance and the center's distance.
            // A higher weight means it adheres more to the center's fall behavior.
            val finalFallDistance = (rawFallDistance * (1.0 - weight) + centerFallDistance * weight).roundToInt()

            columnBlocks.forEach { block ->
                finalSmoke.add(block.below(finalFallDistance))
            }
        }

        return finalSmoke.toList()
    }

    override fun getHitDamageSource(hitEntity: LivingEntity): DamageSource {
        val registryAccess = this.level().registryAccess()
        val damageTypeHolder =
            registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.SMOKEGRENADE_HIT)
        return if (hitEntity == this.owner) {
            DamageSource(damageTypeHolder, this)
        } else {
            DamageSource(damageTypeHolder, this, this.owner)
        }
    }

    fun getSpreadBlocks(): List<BlockPos> {
        if (this.spreadBlocksCache.isEmpty()) {
            this.spreadBlocksCache.addAll(this.entityData.get(spreadBlocksAccessor))
        }
        return this.spreadBlocksCache
    }

    fun canDistinguishFire(position: Vec3): Boolean {
        return this.getSpreadBlocks().any { it.center.distanceToSqr(position) < 2.0 }
    }
}

private class SmokeGrenadeSpreadBlockCalculator(
    val generateCycle: Int,
    val blockPerCycle: Int,
    val stepPerMove: Int,
    val origin: BlockPos,
) {

    // Optimization: Use Sets instead of Lists to avoid duplicates from the start.
    fun calculate(level: Level): Set<BlockPos> {
        val blocks = mutableSetOf<BlockPos>()
        var generatedInLastCycle = mutableSetOf<BlockPos>(origin)
        var generatedInCurrentCycle = mutableSetOf<BlockPos>()
        repeat(generateCycle) {
            repeat(blockPerCycle) {
                generatedInLastCycle.random().let {
                    var currentLocation = it
                    repeat(stepPerMove) {
                        currentLocation = randomMoveOnce(level, currentLocation)
                    }
                    generatedInCurrentCycle.add(currentLocation)
                }
            }
            blocks.addAll(generatedInLastCycle)
            generatedInLastCycle = generatedInCurrentCycle
            generatedInCurrentCycle = mutableSetOf()
        }
        return blocks
    }

    private fun randomMoveOnce(level: Level, blockPos: BlockPos): BlockPos {
        val newLocation = when (randomDirection()) {
            Direction.UP -> blockPos.above() // Corrected
            Direction.DOWN -> blockPos.below() // Corrected
            Direction.NORTH -> blockPos.north()
            Direction.SOUTH -> blockPos.south()
            Direction.WEST -> blockPos.west()
            Direction.EAST -> blockPos.east()
        }
        // Revert to getCollisionShape().isEmpty, as origin sanitization solves the leak.
        if (level.getBlockState(newLocation).getCollisionShape(level, newLocation).isEmpty) {
            return newLocation
        }
        return blockPos
    }

    private fun randomDirection(): Direction {
        return when ((Random.nextInt(10))) {
            0 -> Direction.UP
            1, 5 -> Direction.DOWN
            2, 6 -> Direction.NORTH
            3, 7 -> Direction.SOUTH
            4, 8 -> Direction.WEST
            else -> Direction.EAST

        }
    }
}