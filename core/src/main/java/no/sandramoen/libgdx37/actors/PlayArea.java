package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdx37.utils.AssetLoader;
import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseGame;
import no.sandramoen.libgdx37.utils.GameUtils;

public class PlayArea extends BaseActor {

    public float min_area_size = 4.4f;
    public boolean is_ready_to_remove = false;

    public boolean is_ready = false;
    public boolean is_being_divided = false;

    private Array<Ball> balls;
    private float size_increment = 0f;
    private float size_frequency = 1f;
    private float size_decrement_amount = MathUtils.random(0.0125f, 0.05f);

    public PlayArea(Stage stage, float x, float y, float width, float height) {
        super(x, y, stage);

        loadImage("whitePixel");
        setColor(GameUtils.randomLightColor());

        // body
        setSize(width, height);
        setOrigin(Align.center);
        setBoundaryRectangle(1f);

        //setDebug(true);

        balls = new Array<Ball>();

        float duration = 0.5f;
        addAction(Actions.sequence(
            Actions.delay(MathUtils.random(0f, 0.25f)),
            Actions.scaleTo(1.05f, 1.05f, duration * 0.5f, Interpolation.circleOut),
            Actions.scaleTo(1f, 1f, duration, Interpolation.bounceOut),
            Actions.delay(MathUtils.random(0.1f, 0.4f)),
            Actions.run(() -> {
                addAction(Actions.forever(Actions.sequence(
                    Actions.rotateBy(MathUtils.random(-5f, 5f), 1f)
                )));
            })
        ));
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        /*if (size_increment >= size_frequency) {
            size_increment = 0f;
            if (get_area_size() > min_area_size * size_decrement_amount) {
                addAction(Actions.parallel(
                    Actions.sizeTo(getWidth() - size_decrement_amount, getHeight() - size_decrement_amount, size_frequency)*//*,
                    Actions.moveBy(size_decrement_amount / 2f, size_decrement_amount / 2f, size_frequency)*//*
                ));
                size_decrement_amount += 0.005f;
            }
            else {
                is_ready_to_remove = true;
            }
        } else {
            size_increment += delta;
        }*/

        setBoundaryRectangle(1f);

        for (Ball ball : balls) {
            ball.setWorldBounds(this);

            /*if (get_area_size() < 5f)
                break;*/

            if (ball.is_bounced_horizontal) {
                float amount = 0.005f * ball.getSpeed() * ( 1f / (getWidth() * getHeight()) );
                amount = MathUtils.clamp(amount, 0f, 0.01f);
                if (getScaleX() < 1.2f && getScaleY() < 1.2f)
                    addAction(Actions.sequence(
                        Actions.scaleBy(amount * 1.1f, amount * 0.9f, 0.1f),
                        Actions.scaleBy(-amount * 1.1f, -amount * 0.9f, 0.1f)
                    ));

                ball.is_bounced_horizontal = false;
            } else if (ball.is_bounced_vertical) {
                float amount = 0.005f * ball.getSpeed() * ( 1f / (getWidth() * getHeight()) );
                amount = MathUtils.clamp(amount, 0f, 0.01f);
                if (getScaleX() < 1.2f && getScaleY() < 1.2f)
                    addAction(Actions.sequence(
                        Actions.scaleBy(amount * 0.9f, amount * 1.1f, 0.1f),
                        Actions.scaleBy(-amount * 0.9f, -amount * 1.1f, 0.1f)
                    ));
                ball.is_bounced_vertical = false;
            }
        }
    }


    public float get_area_size() {
        return getWidth() * getHeight();
    }


    @Override
    public boolean remove() {
        if (!balls.isEmpty())
            System.out.println("balls not empty!");

        setTouchable(Touchable.disabled);
        isCollisionEnabled = false;
        balls.clear();
        return super.remove();
    }


    public void remove_split() {
        if (!isCollisionEnabled)
            return;

        isCollisionEnabled = false;
        setTouchable(Touchable.disabled);
        remove();
        /*setZIndex(1);
        float duration = 1.1f;
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(1.1f, 1.1f, duration, Interpolation.exp10Out),
                Actions.color(Color.DARK_GRAY),
                Actions.fadeOut(duration)
            ),
            Actions.run(() -> remove())
        ));*/
    }


    public Array<Ball> get_balls() {
        return balls;
    }


    public boolean contains(Vector2 position) {
        return getBoundaryPolygon().contains(position);
    }


    public void spawn_ball(int index) {
        addAction(Actions.sequence(
            Actions.delay(0.2f * index),
            Actions.run(() -> {
                Ball ball =  new Ball(
                    getStage(),
                    MathUtils.random(0f, getWidth()),
                    MathUtils.random(0f, getHeight())
                );
                add_ball(ball);
                is_ready = true;
                AssetLoader.ball_spawn.play(BaseGame.soundVolume, 1f + (float) (index / 5f), 0);
            })
        ));
    }


    public void add_ball(Ball ball) {
        Vector2 ball_world_position = ball.localToStageCoordinates(new Vector2());

        /*if (ball.getParent() != null && ball.getParent() != this) {
            PlayArea parent = (PlayArea) ball.getParent();
            parent.removeActor(ball);
            parent.get_balls().removeValue(ball, false);
        }*/

        Vector2 new_local_position = stageToLocalCoordinates(ball_world_position);
        ball.setPosition(new_local_position.x, new_local_position.y);

        balls.add(ball);
        addActor(ball);
        ball.setWorldBounds(this);
    }


    public void add_balls(Array<Ball> balls) {
        for (Ball ball : balls) {
            add_ball(ball);
        }
    }


    public void remove_empty() {
        if (!isCollisionEnabled)
            return;

        setTouchable(Touchable.disabled);
        isCollisionEnabled = false;
        float duration = 1.5f;
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(0.25f, 0.25f, duration, Interpolation.exp10Out),
                Actions.fadeOut(duration)
            ),
            Actions.run(() -> remove())
        ));
    }
}
