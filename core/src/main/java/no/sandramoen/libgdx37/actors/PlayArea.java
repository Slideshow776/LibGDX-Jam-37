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
    }


    @Override
    public boolean remove() {
        balls.clear();
        return super.remove();
    }


    public void remove_empty() {
        float duration = 1.5f;
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(0.25f, 0.25f, duration, Interpolation.exp10Out),
                Actions.fadeOut(duration)
            ),
            Actions.run(() -> remove())
        ));
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
}
