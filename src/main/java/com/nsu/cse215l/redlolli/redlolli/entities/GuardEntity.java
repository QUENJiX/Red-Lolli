package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * Implements localized, static environmental hazards explicitly barring traversal into designated sanctuary nodes.
 * Enforces rigid geometric barriers that necessitate transient interaction logic (e.g., distractions) to bypass securely.
 * Governs differential difficulty scales programmatically shifting vulnerability windows strictly based on instantiation type.
 */
public class GuardEntity extends Entity implements Collidable {

    /**
     * Categorizes the physiological archetype of the static hazard dictating precise internal latency constraints.
     */
    public enum Type {
        BAT,
        COBRA,
        CENTIPEDE
    }

    private final Type type;

    private final int escapeRow;
    private final int escapeCol;

    private boolean distracted = false;
    private double distractionTimer = 0;

    private static final int BAT_DISTRACTION_DURATION = 300; 
    private static final int COBRA_DISTRACTION_DURATION = 180; 
    private static final int CENTIPEDE_DISTRACTION_DURATION = 120; 

    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    /**
     * Constructs the stationary deterrent coupling its presence explicitly to constrained spatial coordinates.
     *
     * @param x Initial longitudinal location natively defining the entity's rendering center.
     * @param y Initial latitudinal location natively defining the entity's rendering center.
     * @param type The operational archetype defining the difficulty class and subsequent vulnerability margins.
     * @param escapeRow The precise matrix row identifying the targeted sanctuary tile this instance protects.
     * @param escapeCol The precise matrix column identifying the targeted sanctuary tile this instance protects.
     */
    public GuardEntity(double x, double y, Type type, int escapeRow, int escapeCol) {
        super(x, y, 28.0);
        this.type = type;
        this.escapeRow = escapeRow;
        this.escapeCol = escapeCol;
    }

    /**
     * Processes independent clock logic ensuring localized vulnerability transitions map identically across unstable 
     * framerate iterations sequentially.
     */
    @Override
    public void update() {
        long now = System.nanoTime();
        if (lastUpdateTime == 0) lastUpdateTime = now;
        
        // Isolate processing intervals mapping literal seconds sequentially validating standard tick metrics natively
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        // Erode active vulnerability margins iteratively executing immediate state resets fundamentally upon expiration
        if (distracted) {
            distractionTimer -= timeDelta;
            if (distractionTimer <= 0) {
                distracted = false;
                distractionTimer = 0;
            }
        }
    }

    /**
     * Injects transient compliance explicitly decoupling the entity's default lethality checks for a mathematically derived limit.
     * Modulates operational constraints explicitly mirroring the entity's instantiated archetype universally.
     */
    public void distract() {
        if (!distracted) {
            distracted = true;
            
            // Assign chronological vulnerability limits linearly constrained by increasing game-cycle difficulty
            distractionTimer = type == Type.BAT ? BAT_DISTRACTION_DURATION : type == Type.COBRA ? COBRA_DISTRACTION_DURATION : CENTIPEDE_DISTRACTION_DURATION;
        }
    }

    /**
     * Validates rigorous polygon intersection tests mapping protagonist rectangular arrays against the precisely restricted sanctuary node.
     * Iterates explicitly across varied cardinal sub-points avoiding singular theoretical overlap failures unilaterally.
     *
     * @param playerHitbox Extracted dimensional bounds characterizing the external intrusive entity exactly.
     * @return boolean True only if localized protagonist coordinates directly overlap into the secured topological boundary.
     */
    public boolean isPlayerOnGuardedRoom(Hitbox2D playerHitbox) {
        double cx = (playerHitbox.getMinX() + playerHitbox.getMaxX()) / 2;
        double cy = (playerHitbox.getMinY() + playerHitbox.getMaxY()) / 2;

        // Construct supplementary dimensional validations spanning peripheral array extremeties precisely mapped
        double[][] points = {
                { cx, cy },
                { playerHitbox.getMinX() + 2, cy },
                { playerHitbox.getMaxX() - 2, cy },
                { cx, playerHitbox.getMinY() + 2 },
                { cx, playerHitbox.getMaxY() - 2 }
        };

        for (double[] p : points) {
            int c = (int) (p[0] / Maze.TILE_SIZE);
            int r = (int) ((p[1] - Maze.Y_OFFSET) / Maze.TILE_SIZE);
            
            // Assure fatal logic exclusively when the exact matrix node intersection completes natively
            if (r == escapeRow && c == escapeCol) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies active temporal override limits granting localized immunity identically spanning interactions linearly.
     *
     * @return boolean True precisely signaling ongoing suppressed lethality conditions externally.
     */
    public boolean isDistracted() {
        return distracted;
    }

    /**
     * Quantifies Euclidean distances resolving explicit topological boundaries scaling natively instead of raw Cartesian metrics natively.
     *
     * @param playerX Baseline localized horizontal Cartesian limit extracting current proximity arrays securely.
     * @param playerY Baseline localized vertical Cartesian limit extracting current proximity arrays securely.
     * @return double Explicit vector magnitude mathematically delineating relative geographic divergence globally.
     */
    public double distanceToPlayerInTiles(double playerX, double playerY) {
        double dx = Math.abs(playerX - x) / Maze.TILE_SIZE;
        double dy = Math.abs(playerY - y) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Checks if external interactions execute within valid geographic distances authorizing explicitly modeled tactical engagements seamlessly.
     *
     * @param playerX Coordinate variable parsing proximal intersection ranges radially.
     * @param playerY Coordinate variable parsing proximal intersection ranges radially.
     * @return boolean True unconditionally evaluating distance verifications below static threshold scales identically.
     */
    public boolean isWithinDistractionRange(double playerX, double playerY) {
        return distanceToPlayerInTiles(playerX, playerY) <= 3.0;
    }

    /**
     * Renders foundational spatial allocation variables establishing external lethality boundaries symmetrically mapped unconditionally.
     *
     * @return Hitbox2D Explicit rectangular encapsulation structurally governing collision limits externally.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * Translates immutable instantiation categories interpreting localized visual rendering decisions externally identical.
     *
     * @return Type Concrete archetype classification natively mapped.
     */
    public Type getType() {
        return type;
    }
}
