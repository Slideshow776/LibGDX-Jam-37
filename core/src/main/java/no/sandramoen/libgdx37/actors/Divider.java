package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx37.utils.BaseActor;

public class Divider extends BaseActor {

     public enum Going {
         UP,
         RIGHT,
         DOWN,
         LEFT
    }

    private Going going;
    private float speed = 0.01f;
    private boolean is_growing = true;

    public Divider(Stage stage, Vector2 position, PlayArea area, Going going) {
        super(position.x, position.y, stage);

        loadImage("whitePixel");

        // body
        setSize(0.25f, 0.25f);
        setOrigin(Align.center);
        setBoundaryRectangle(1f);

        Vector2 divider_world_position = localToStageCoordinates(new Vector2());
        Vector2 new_local_position = area.stageToLocalCoordinates(divider_world_position);
        setPosition(
            new_local_position.x - getWidth() * 0.8f,
            new_local_position.y - getHeight() * 0.2f
        );

        //setDebug(true);
        setWorldBounds(area);

        this.going = going;
        if (going == Going.UP) {
            setColor(Color.GOLDENROD);
            setOrigin(Align.bottom);
        } else if (going == Going.RIGHT) {
            setOrigin(Align.left);
            setColor(Color.FOREST);
        } else if (going == Going.DOWN) {
            setOrigin(Align.top);
            setColor(Color.BLUE);
        } else if (going == Going.LEFT) {
            setOrigin(Align.right);
            setColor(Color.OLIVE);
        }
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        stop_against_world_bounds();

        if (is_growing) {
            if (going == Going.UP || going == Going.DOWN) {
                setScaleY(getScaleY() + speed);
            } else if (going == Going.RIGHT || going == Going.LEFT) {
                setScaleX(getScaleX() + speed);
            }
        }
    }


    private void stop_against_world_bounds() {
        if (going == Going.UP) {
            if (getY() + ( getHeight() * getScaleY() ) > worldBounds.height) {
                is_growing = false;
            }
        } else if (going == Going.RIGHT) {
            if (getX() + ( getWidth() * getScaleX() ) > worldBounds.width) {
                is_growing = false;
            }
        } else if (going == Going.DOWN) {
            if (getY() + getHeight() - (getHeight() * getScaleY()) < 0) {
                is_growing = false;
            }
        } else if (going == Going.LEFT) {
            if (getX() + getWidth() - (getWidth() * getScaleX()) < 0) {
                is_growing = false;
            }
        }
    }
}
