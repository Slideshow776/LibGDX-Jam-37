package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx37.utils.AssetLoader;
import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseGame;

public class Ball extends BaseActor {

    public static final float ORIGINAL_SPEED = MathUtils.random(1.25f, 3.25f);

    public boolean is_bounced_vertical = false;
    public boolean is_bounced_horizontal = false;

    private float speed = ORIGINAL_SPEED;
    private float movementAcceleration = speed * 0.75f;

    public Ball(Stage stage, float x, float y, float speed) {
        super(x, y, stage);

        loadImage("whitePixel");
        setColor(
            MathUtils.random(1f, 1f),
            MathUtils.random(0f, 0.3f),
            MathUtils.random(0f, 0.3f),
            1f
        );
        setTouchable(Touchable.disabled);

        // body
        float random_size = MathUtils.random(0.2f, 0.3f);
        setSize(random_size, random_size);
        setOrigin(Align.center);
        setBoundaryRectangle(1f);

        //setDebug(true);

        // movement
        this.speed = speed;
        setAcceleration(movementAcceleration);
        setMaxSpeed(speed * 10f);
        setDeceleration(movementAcceleration);

        velocityVec = new Vector2(
            MathUtils.random(-1f, 1f),
            MathUtils.random(-1f, 1f)
        );

        setOrigin(Align.center);
        addAction(Actions.sequence(
            Actions.scaleTo(0f, 0f, 0f),
            //Actions.delay(1f),
            Actions.scaleTo(2.5f, 2.5f, 0.5f, Interpolation.bounceOut),
            Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.bounceOut)
        ));

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
        if (!isCollisionEnabled)
            return;

        isCollisionEnabled = false;
        float duration = 1.5f;
        AssetLoader.ball_death.play(BaseGame.soundVolume, MathUtils.random(0.9f, 1.1f), 0f);
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(0.25f, 0.25f, duration, Interpolation.exp10Out),
                Actions.rotateBy(MathUtils.random(-360f, 360f), duration),
                Actions.fadeOut(duration)
            ),
            Actions.run(() -> remove())
        ));
    }


    private void play_bounce_sound() {
        AssetLoader.ball_bounce.play(BaseGame.soundVolume * 0.5f, 0.6f + speed / 4f, 0f);
        speed += 0.01f;
    }


    private void bounce_against_world_bounds() {
        // x
        if (getX() < 0) {
            setX(0f);
            velocityVec.x *= -1;
            squish_against_horizontal_bounds();
            play_bounce_sound();
        } else if (getX() + getWidth() > worldBounds.width) {
            setX(worldBounds.width - getWidth());
            velocityVec.x *= -1;
            squish_against_horizontal_bounds();
            play_bounce_sound();
        }

        // y
        if (getY() < 0) {
            setY(0f);
            velocityVec.y *= -1;
            squish_against_vertical_bounds();
            play_bounce_sound();
        } else if (getY() + getHeight() > worldBounds.height) {
            setY(worldBounds.height - getHeight());
            velocityVec.y *= -1;
            squish_against_vertical_bounds();
            play_bounce_sound();
        }
    }


    private void squish_against_vertical_bounds() {
        is_bounced_horizontal = true;
        float duration = 0.25f * speed / 10;
        float amount = speed / 5;
        amount = MathUtils.clamp(amount, 0f, 0.5f);
        addAction(Actions.sequence(
            Actions.scaleTo(1 + amount, 1 - amount, duration),
            Actions.scaleTo(1f, 1f, duration)
        ));
    }


    private void squish_against_horizontal_bounds() {
        is_bounced_vertical = true;
        float duration = 0.25f * speed / 5;
        float amount = speed / 5;
        amount = MathUtils.clamp(amount, 0f, 0.5f);
        addAction(Actions.sequence(
            Actions.scaleTo(1 - amount, 1 + amount, duration),
            Actions.scaleTo(1f, 1f, duration)
        ));
    }
}
