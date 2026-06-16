package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.GameUtils;

public class PlayArea extends BaseActor {

    private Array<Ball> balls;
    private float size_increment = 0f;
    private float size_frequency = 1f;
    private float size_decrement_amount = MathUtils.random(0.025f, 0.075f);

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
            Actions.scaleTo(1f, 1f, duration, Interpolation.bounceOut)//,
            /*Actions.run(() -> {
                // heart rate animation
                addAction(Actions.forever(Actions.sequence(
                    Actions.scaleTo(1.005f, 1.005f, 0.2f, Interpolation.exp10Out),
                    Actions.scaleTo(1f, 1f, 0.8f, Interpolation.bounceOut),
                    Actions.delay(0.4f)
                )));
            })*/
        ));
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        if (size_increment >= size_frequency) {
            size_increment = 0f;
            if (getWidth() > 0.34f && getHeight() > 0.34f)
                addAction(Actions.parallel(
                    Actions.sizeTo(getWidth() - size_decrement_amount, getHeight() - size_decrement_amount, size_frequency),
                    Actions.moveBy(size_decrement_amount / 2f, size_decrement_amount / 2f, size_frequency)
                ));
            //System.out.println(getWidth() + ", " + getHeight());
        } else {
            size_increment += delta;
        }

        setBoundaryRectangle(1f);


        if (balls.isEmpty())
            remove_empty();

        for (Ball ball : balls) {
            ball.setWorldBounds(this);
            if (ball.is_bounced_horizontal) {
                float amount = 0.005f * ball.getSpeed() * ( 1 / (getWidth() * getHeight()) );
                if (getScaleX() < 1.2f && getScaleY() < 1.2f)
                    addAction(Actions.sequence(
                        Actions.scaleBy(amount * 1.1f, amount * 0.9f, 0.1f),
                        Actions.scaleBy(-amount * 1.1f, -amount * 0.9f, 0.1f)
                    ));

                ball.is_bounced_horizontal = false;
            } else if (ball.is_bounced_vertical) {
                float amount = 0.005f * ball.getSpeed() * ( 1 / (getWidth() * getHeight()) );
                if (getScaleX() < 1.2f && getScaleY() < 1.2f)
                    addAction(Actions.sequence(
                        Actions.scaleBy(amount * 0.9f, amount * 1.1f, 0.1f),
                        Actions.scaleBy(-amount * 0.9f, -amount * 1.1f, 0.1f)
                    ));
                ball.is_bounced_vertical = false;
            }
        }
    }


    @Override
    public boolean remove() {
        balls.clear();
        return super.remove();
    }


    public void remove_split() {
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


    public void spawn_ball() {
        Ball ball =  new Ball(
            getStage(),
            MathUtils.random(0f, getWidth()),
            MathUtils.random(0f, getHeight())
        );
        add_ball(ball);
    }


    public void add_ball(Ball ball) {
        Vector2 ball_world_position = ball.localToStageCoordinates(new Vector2());

        if (ball.getParent() != null)
            ball.getParent().removeActor(ball);

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


    private void remove_empty() {
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
