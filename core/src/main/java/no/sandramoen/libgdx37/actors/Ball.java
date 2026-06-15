package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseGame;

public class Ball extends BaseActor {

    private float speed = MathUtils.random(2.5f, 6.5f);
    private float movementAcceleration = speed * 0.75f;

    public Ball(Stage stage, float x, float y) {
        super(x, y, stage);

        loadImage("whitePixel");
        setColor(Color.FIREBRICK);

        // body
        setSize(0.25f, 0.25f);
        setOrigin(Align.center);
        setBoundaryRectangle(1f);

        //setDebug(true);

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

        if (worldBounds == null)
            return;

        setSpeed(speed);
        bounce_against_world_bounds();
        applyPhysics(delta);
    }


    public void remove_lost() {
        float duration = 1.5f;
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(0.25f, 0.25f, duration, Interpolation.exp10Out),
                Actions.rotateBy(MathUtils.random(-360f, 360f), duration),
                Actions.fadeOut(duration)
            ),
            Actions.run(() -> remove())
        ));
    }


    private void bounce_against_world_bounds() {
        // x
        if (getX() < 0) {
            setX(0f);
            velocityVec.x *= -1;
            squish_against_horizontal_bounds();
        } else if (getX() + getWidth() > worldBounds.width) {
            setX(worldBounds.width - getWidth());
            velocityVec.x *= -1;
            squish_against_horizontal_bounds();
        }

        // y
        if (getY() < 0) {
            setY(0f);
            velocityVec.y *= -1;
            squish_against_vertical_bounds();
        }else if (getY() + getHeight() > worldBounds.height) {
            setY(worldBounds.height - getHeight());
            velocityVec.y *= -1;
            squish_against_vertical_bounds();
        }
    }


    private void squish_against_vertical_bounds() {
        float duration = 0.25f * speed / 10;
        addAction(Actions.sequence(
            Actions.scaleTo(1 + speed / 10, 1 - speed / 10, duration),
            Actions.scaleTo(1f, 1f, duration)
        ));
    }


    private void squish_against_horizontal_bounds() {
        float duration = 0.25f * speed / 10;
        addAction(Actions.sequence(
            Actions.scaleTo(1 - speed / 10, 1 + speed / 10, duration),
            Actions.scaleTo(1f, 1f, duration)
        ));
    }
}
