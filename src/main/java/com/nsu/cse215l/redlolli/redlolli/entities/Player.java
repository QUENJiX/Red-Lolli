package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * Encapsulates the protagonist's localized state, encompassing geometric
 * displacement,
 * resource governance (stamina and sanity), and temporal simulation
 * synchronization.
 * Operates as the central physical entity subject to environmental restrictions
 * and adversarial tracking.
 */
public class Player extends Entity implements Collidable {

    private static final double BASE_SPEED = 3.2;
    private static final double SPRINT_MULTIPLIER = 1.8;
    private static final int MAX_STAMINA_FRAMES = 180;
    private static final int EXHAUSTED_FRAMES = 180;

    private boolean isInEscapeRoom = false;
    private double staminaFrames = MAX_STAMINA_FRAMES;
    private double exhaustedFrames = 0;
    private double facingX = 1;
    private double facingY = 0;

    private static final int MAX_SANITY = 100;
    private static final int PASSIVE_DRAIN_INTERVAL = 60;
    private static final int NEAR_LUNA_DRAIN_INTERVAL = 30;
    private static final int ESCAPE_ROOM_RECOVERY_INTERVAL = 60;
    private static final int NEAR_LUNA_DISTANCE = 150;
    private int sanity = MAX_SANITY;
    private double sanityDrainCounter = 0;
    private boolean isNearLuna = false;
    private boolean sanityDead = false;

    private int animFrame = 0;
    private double animTimer = 0;
    private boolean isMoving = false;
    private boolean movedThisFrame = false;

    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    /**
     * Constructs the protagonist at predefined Cartesian coordinates seamlessly
     * defining initial geometric allocation.
     *
     * @param x The initial longitudinal Cartesian position mapped natively.
     * @param y The initial latitudinal Cartesian position mapped natively.
     */
    public Player(double x, double y) {
        super(x, y, 20.0);
    }

    /**
     * Processes independent temporal mutations validating fatigue recovery loops
     * and psychological degradation natively.
     * Integrates time-delta normalizations averting logic desynchronization across
     * variable frame delivery scales.
     */
    @Override
    public void update() {
        long now = System.nanoTime();
        if (lastUpdateTime == 0)
            lastUpdateTime = now;

        // Formulate relative delta multipliers standardizing 60hz equivalents
        // analytically across differing processing intervals
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        // Process punitive physical restrictions governing overexertion organically
        if (exhaustedFrames > 0) {
            exhaustedFrames -= timeDelta;
        }

        // Interpolate cosmetic progression frames contingent specifically upon
        // deliberate spatial translation
        if (movedThisFrame) {
            animTimer += timeDelta;
            if (animTimer > 10) {
                animFrame++;
                animTimer = 0;
            }
        } else {
            animFrame = 0;
            animTimer = 0;
        }

        isMoving = movedThisFrame;
        movedThisFrame = false;

        // Arbitrate final mortality consequences mapping psychological exhaustion
        // limits natively
        if (sanity <= 0) {
            sanityDead = true;
            sanity = 0;
            return;
        }

        if (isInEscapeRoom) {
            // Induce systemic psychological recovery strictly mapping sanctuary thresholds
            // linearly
            sanityDrainCounter += timeDelta;
            if (sanityDrainCounter >= ESCAPE_ROOM_RECOVERY_INTERVAL && sanity < MAX_SANITY) {
                sanity++;
                sanityDrainCounter = 0;
            }
        } else {
            // Apply psychological degradation metrics exponentially weighted by adversarial
            // proximity radii
            sanityDrainCounter += timeDelta;
            int drainInterval = isNearLuna ? NEAR_LUNA_DRAIN_INTERVAL : PASSIVE_DRAIN_INTERVAL;
            if (sanityDrainCounter >= drainInterval) {
                sanity--;
                sanityDrainCounter = 0;
                if (sanity <= 0) {
                    sanity = 0;
                    sanityDead = true;
                }
            }
        }
    }

    /**
     * Translates geometric bounds validating explicit wall intersections
     * independently restricting Cartesian updates.
     * Decrements physical reserves natively upon intensive maneuvering protocols.
     *
     * @param dx        Theoretical localized lateral velocity multiplier dictating
     *                  longitudinal shifts.
     * @param dy        Theoretical localized vertical velocity multiplier dictating
     *                  latitudinal shifts.
     * @param maze      The fundamental topological map granting collision
     *                  intersection abstractions statically.
     * @param sprinting Boolean mandate triggering accelerated translation metrics
     *                  organically.
     */
    public void move(double dx, double dy, Maze maze, boolean sprinting) {
        double speed = getMovementSpeed(sprinting) * timeDelta;

        double nextX = this.x + (dx * speed);
        double nextY = this.y + (dy * speed);

        Hitbox2D nextHitbox = new Hitbox2D(nextX, nextY, size, size);

        // Sanction spatial relocation solely upon passing definitive obstruction
        // validations natively
        if (!maze.isWallCollision(nextHitbox)) {
            this.x = nextX;
            this.y = nextY;

            // Retain recent movement vectors governing explicit aesthetic or interaction
            // alignments sequentially
            if (dx != 0 || dy != 0) {
                facingX = dx;
                facingY = dy;
                movedThisFrame = true;
            }
        }

        // Dictate metabolic resource degradation factoring intentional sprint inputs
        // mathematically alongside existing exhaustion states
        if (sprinting && exhaustedFrames == 0 && staminaFrames > 0 && (dx != 0 || dy != 0)) {
            staminaFrames -= timeDelta;
            if (staminaFrames <= 0) {
                staminaFrames = 0;
                exhaustedFrames = EXHAUSTED_FRAMES;
            }
        } else if (!sprinting && exhaustedFrames == 0 && staminaFrames < MAX_STAMINA_FRAMES) {
            // Enact gradual regeneration organically offsetting depleted parameter values
            // proportionally
            staminaFrames += 0.5 * timeDelta;
            if (staminaFrames > MAX_STAMINA_FRAMES) {
                staminaFrames = MAX_STAMINA_FRAMES;
            }
        }
    }

    /**
     * Extracts active locomotive status validating aesthetic playback requirements
     * inherently.
     *
     * @return boolean True exclusively if definitive spatial alterations occurred
     *         fundamentally this frame.
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Harvests calculated cosmetic integer mapping current spatial cycle
     * projections sequentially.
     *
     * @return int Active cosmetic frame identifier validating render loop parity.
     */
    public int getAnimFrame() {
        return animFrame;
    }

    /**
     * Quantifies geographic overlap bounding constraints conforming to core
     * systemic checks identically.
     *
     * @return Hitbox2D Geometric representation detailing current occupancy
     *         coordinates dynamically.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * Overrides internal aesthetic variables accommodating intense adversarial
     * pursuit scenarios linearly.
     *
     * @param chased Boolean flag indicating active aggressive trailing metrics.
     */
    public void setBeingChased(boolean chased) {
    }

    /**
     * Interrogates spatial sanctuary verifications overriding generic lethal
     * implications natively.
     *
     * @return boolean True strictly traversing designated non-hostile topology
     *         zones dynamically.
     */
    public boolean isInEscapeRoom() {
        return isInEscapeRoom;
    }

    /**
     * Formats transient parameter dictates mapping subjective sanctuary integration
     * independently.
     *
     * @param inEscapeRoom Override boolean defining unmitigable geometric safety
     *                     validations.
     */
    public void setInEscapeRoom(boolean inEscapeRoom) {
        this.isInEscapeRoom = inEscapeRoom;
    }

    /**
     * Validates persistent metabolic punitive statuses explicitly circumventing
     * sprint triggers temporarily.
     *
     * @return boolean True intrinsically mapped to unrecovered stamina deficits
     *         natively.
     */
    public boolean isExhausted() {
        return exhaustedFrames > 0;
    }

    /**
     * Checks fundamental resource pools verifying valid thresholds requisite for
     * accelerated traversal identically.
     *
     * @return boolean True contingent precisely upon sufficient reserves excluding
     *         exhausted consequences natively.
     */
    public boolean canSprint() {
        return staminaFrames > 0 && exhaustedFrames == 0;
    }

    /**
     * Converts quantified biological reserves mapping normalized percentage scalars
     * mathematically.
     *
     * @return double Scalar ratio extracting immediate capacity proportionally.
     */
    public double getStaminaPercent() {
        return (double) staminaFrames / MAX_STAMINA_FRAMES;
    }

    /**
     * Isolates explicitly preserved longitudinal orientation mapping recent
     * translation metrics iteratively.
     *
     * @return double Active Cartesian vector facing alignment horizontally.
     */
    public double getFacingX() {
        return facingX;
    }

    /**
     * Isolates explicitly preserved latitudinal orientation mapping recent
     * translation metrics iteratively.
     *
     * @return double Active Cartesian vector facing alignment vertically.
     */
    public double getFacingY() {
        return facingY;
    }

    /**
     * Correlates geometric divergence parsing relative Euclidean distances
     * analyzing antagonistic proximities definitively.
     * Aggravates psychological decline mathematically weighting severe proximity
     * values unconditionally.
     *
     * @param lunaX Adversarial centralized longitudinal metric directly correlated.
     * @param lunaY Adversarial centralized latitudinal metric directly correlated.
     */
    public void updateNearLunaStatus(double lunaX, double lunaY) {
        double dx = this.x - lunaX;
        double dy = this.y - lunaY;

        // Execute literal Pythagorean formulations circumventing broader grid
        // abstractions exclusively for radial evaluations natively
        double dist = Math.sqrt(dx * dx + dy * dy);
        this.isNearLuna = dist < NEAR_LUNA_DISTANCE;
    }

    /**
     * Determines numerical psychological retention capacities influencing failure
     * constraints seamlessly.
     *
     * @return int Exact physiological integer representing sanity intrinsically.
     */
    public int getSanity() {
        return sanity;
    }

    /**
     * Manipulates psychological stability allocations governing external systemic
     * interactions natively.
     *
     * @param value Definitive absolute integer mapping renewed capacities
     *              statically.
     */
    public void setSanity(int value) {
        this.sanity = value;
    }

    /**
     * Extracts psychological threshold calculations linearly mapping normalized
     * fractional scalars logically.
     *
     * @return double Scalar ratio communicating subjective sanity deficits
     *         dynamically.
     */
    public double getSanityPercent() {
        return (double) sanity / MAX_SANITY;
    }

    /**
     * Affirms conclusive physiological failure states directly interpreted
     * intrinsically altering core rendering consequences.
     *
     * @return boolean True universally signaling insurmountable psychological
     *         degradation fundamentally.
     */
    public boolean isSanityDead() {
        return sanityDead;
    }

    /**
     * Asserts absolute physiological reclamation protocols restoring core
     * capacities statically.
     * Functions predominantly traversing overarching temporal states mapping
     * sequential load instructions inherently.
     */
    public void resetSanity() {
        this.sanity = MAX_SANITY;
        this.sanityDrainCounter = 0;
        this.isNearLuna = false;
        this.sanityDead = false;
    }

    /**
     * Configures theoretical translation multipliers weighing independent metabolic
     * flags statically against baseline norms.
     *
     * @param sprinting Boolean objective mapping preferred velocity parameters
     *                  conditionally.
     * @return double Processed velocity scale reflecting physical exhaustion logic
     *         linearly.
     */
    private double getMovementSpeed(boolean sprinting) {
        // Enforce punitive reductions overriding all subsequent checks mechanically
        // mapped to physiological limits
        if (isExhausted()) {
            return BASE_SPEED * 0.6;
        }

        // Assign boosted metrics explicitly acknowledging biological verifications
        // linearly
        if (sprinting && canSprint()) {
            return BASE_SPEED * SPRINT_MULTIPLIER;
        }
        return BASE_SPEED;
    }
}
