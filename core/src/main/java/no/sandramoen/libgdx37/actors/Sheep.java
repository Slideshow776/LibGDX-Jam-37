package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.QuadTreeFloat;
import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseGame;

public class Sheep extends BaseActor {

    public boolean is_ready_to_be_removed;
    public int scoreMultiplier = 1;

    private final float BORDER_DISTANCE_THRESHOLD = 0.5f;
    private final float PLAYER_DISTANCE_THRESHOLD = 4.5f;
    private final float ALIGNMENT_THRESHOLD = 1.0f;
    private final float COHESION_THRESHOLD = 1.2f;

    private float walkingSpeed = 0.125f + MathUtils.random(-0.05f, 0.05f);
    private float runningSpeed = 0.5f + MathUtils.random(-0.05f, 0.1f);
    private float movementAcceleration = runningSpeed * 0.75f + MathUtils.random(-0.05f, 0.05f);

    private static final FloatArray results = new FloatArray(256);


    public Sheep(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("whitePixel");
        setColor(new Color(0xf2d1d3FF));

        // body
        setSize(
            0.11f + MathUtils.random(-0.05f, 0.05f),
            0.2f
        );
        setSize(getWidth(), getWidth() * 2f + + MathUtils.random(-0.05f, 0.05f));
        centerAtPosition(x, y);
        setOrigin(Align.center);
        setBoundaryRectangle(0.5f);
        //setBoundaryPolygon(8, 0.9f);

        //setWorldBounds(BaseGame.WORLD_WIDTH + 0.5f, BaseGame.WORLD_HEIGHT);

        // movement
        setAcceleration(movementAcceleration);
        setMaxSpeed(runningSpeed);
        setDeceleration(movementAcceleration);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        if (isCollisionEnabled)
            applyPhysics(delta);
    }


    public boolean isMoving() {
        return getSpeed() > BaseGame.MOVEMENT_THRESHOLD;
    }


    public void die() {
        if (!isCollisionEnabled)
            return;

        isCollisionEnabled = false;
        float duration = 0.5f;
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveBy(-.5f, 0.0f, duration),
                Actions.fadeOut(duration)
            ),
            Actions.run(() -> is_ready_to_be_removed = true)
        ));
    }


    public void herded() {
        if (!isCollisionEnabled)
            return;

        isCollisionEnabled = false;
        is_ready_to_be_removed = true;
    }


    public void updateBehaviour(float playerX, float playerY, Array<Sheep> sheep, QuadTreeFloat quad) {
        if (!isCollisionEnabled)
            return;

        boolean isPlayerClose = Vector2.dst(getX(), getY(), playerX, playerY) < PLAYER_DISTANCE_THRESHOLD;
        setMaxSpeed(isPlayerClose ? runningSpeed : walkingSpeed);

        avoidBorders();
        avoidPlayer(playerX, playerY);

        if (isPlayerClose) {
            applyAlignment(sheep, quad);
            applyCohesion(sheep, quad);
        } else {
            wander();
        }
        applySeparation(sheep, quad);

        setRotation(getMotionAngle() - 90);
    }


    private void avoidBorders() {
        if (get_center_position().x < BORDER_DISTANCE_THRESHOLD) { // left border
            accelerateAtAngle(0);
            setRotation(getMotionAngle() - 90);
        } else if (get_center_position().x > BaseGame.WORLD_WIDTH - BORDER_DISTANCE_THRESHOLD) { // right border
            accelerateAtAngle(180);
            setRotation(getMotionAngle() - 90);
        } else if (get_center_position().y < BORDER_DISTANCE_THRESHOLD) { // bottom border
            accelerateAtAngle(90);
            setRotation(getMotionAngle() - 90);
        }/* else if (get_center_position().y > BaseGame.WORLD_HEIGHT - BORDER_DISTANCE_THRESHOLD) { // top border
            accelerateAtAngle(270);
            setRotation(getMotionAngle() - 90);
            return;
        }*/
    }


    private void avoidPlayer(float playerX, float playerY) {
        Vector2 away_from_player = get_center_position().sub(playerX, playerY);
        float distance_to_player = away_from_player.len();

        if (distance_to_player < PLAYER_DISTANCE_THRESHOLD) {
            accelerateAtAngle(away_from_player.angleDeg());
            setRotation(getMotionAngle() - 90);
        }
    }


    private void applyAlignment(Array<Sheep> sheep, QuadTreeFloat quad) {
        Vector2 alignment = new Vector2();
        int alignmentCount = 0;

        results.clear();
        quad.query(getX(Align.center), getY(Align.center), ALIGNMENT_THRESHOLD, results);
        for (int i = 0, n = results.size; i < n; i+= 4) {
            int idx = (int) results.get(i);
            Sheep other = sheep.get(idx);
            if (other == this)
                continue;
            alignment.add(other.velocityVec);
            alignmentCount++;
        }

        if (alignmentCount > 0) {
            alignment.scl(1f / alignmentCount);
            accelerateAtAngle(alignment.angleDeg());
        }
    }


    private void applyCohesion(Array<Sheep> sheep, QuadTreeFloat quad) {
        Vector2 cohesion = new Vector2();
        int cohesionCount = 0;


        results.clear();
        quad.query(getX(Align.center), getY(Align.center), COHESION_THRESHOLD, results);
        for (int i = 0, n = results.size; i < n; i+= 4) {
            int idx = (int) results.get(i);
            Sheep other = sheep.get(idx);
            if (other == this)
                continue;

            cohesion.add(other.getX(Align.center), other.getY(Align.center));
            cohesionCount++;
        }

        if (cohesionCount > 0) {
            cohesion.scl(1f / cohesionCount);
            cohesion.sub(getX(Align.center), getY(Align.center));
            accelerateAtAngle(cohesion.angleDeg());
        }
    }


    private void applySeparation(Array<Sheep> sheep, QuadTreeFloat quad) {
        results.clear();
        quad.query(getX(Align.center), getY(Align.center), 0.5f, results);
        for (int i = 0, n = results.size; i < n; i+= 4) {
            int idx = (int)results.get(i);
            Sheep other = sheep.get(idx);
            if (other == this)
                continue;

            Vector2 normal = preventOverlap(other);
            if (normal != null)
                accelerateAtAngle(normal.angleDeg());
        }
    }


    private void wander() {
        if (MathUtils.random() < 0.05f) {
            float random_angle = MathUtils.random(-10f, 10f) * Gdx.graphics.getDeltaTime();
            setMotionAngle(getMotionAngle() + random_angle);
        }
    }
}
