package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseGame;

public class Ball extends BaseActor {

    private float speed = 4.0f;
    private float movementAcceleration = speed * 0.75f;

    public Ball(Stage stage) {
        super(0f, 0f, stage);

        loadImage("whitePixel");
        setColor(Color.FIREBRICK);

        // body
        setSize(0.25f, 0.25f);
        setOrigin(Align.center);
        setBoundaryRectangle(1f);

        setDebug(true);

        // movement
        setAcceleration(movementAcceleration);
        setMaxSpeed(speed);
        setDeceleration(movementAcceleration);

        velocityVec = new Vector2(
            MathUtils.random(-1f, 1f),
            MathUtils.random(-1f, 1f)
        );
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        setSpeed(speed);
        bounce_against_world_bounds();
        applyPhysics(delta);
    }


    private void bounce_against_world_bounds() {
        // x
        if (getX() < 0) {
            setX(0f);
            velocityVec.x *= -1;
        } else if (getX() + getWidth() > worldBounds.width) {
            setX(worldBounds.width - getWidth());
            velocityVec.x *= -1;
        }

        // y
        if (getY() < 0) {
            setY(0f);
            velocityVec.y *= -1;
        }else if (getY() + getHeight() > worldBounds.height) {
            setY(worldBounds.height - getHeight());
            velocityVec.y *= -1;
        }
    }
}
