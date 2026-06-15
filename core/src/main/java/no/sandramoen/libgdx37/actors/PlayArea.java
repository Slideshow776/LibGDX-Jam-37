package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseGame;

public class PlayArea extends BaseActor {

    private Array<Ball> balls;

    public PlayArea(Stage stage, float x, float y, float width, float height) {
        super(x, y, stage);

        loadImage("whitePixel");
        setColor(Color.GRAY);

        // body
        setSize(width, height);
        setOrigin(Align.center);
        setBoundaryRectangle(1f);

        //setDebug(true);

        balls = new Array<Ball>();
    }


    public Array<Ball> get_balls() {
        return balls;
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
