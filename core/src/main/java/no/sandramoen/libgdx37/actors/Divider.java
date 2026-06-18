package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.GameUtils;

public class Divider extends BaseActor {

    public static final float SIZE = 0.25f;
    public enum Going {
         UP,
         RIGHT,
         DOWN,
         LEFT
    }
    public boolean is_growing = true;
    public boolean is_horizontal = false;

    private Going going;
    private float speed = 0.01f;

    public Divider(Stage stage, Vector2 position, PlayArea area, Going going) {
        super(position.x, position.y, stage);

        loadImage("whitePixel");
        setTouchable(Touchable.disabled);

        // body
        setSize(SIZE, SIZE);
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
            setOrigin(Align.bottom);
            //setColor(Color.GOLDENROD);
        } else if (going == Going.RIGHT) {
            is_horizontal = true;
            setOrigin(Align.left);
            //setColor(Color.FOREST);
        } else if (going == Going.DOWN) {
            setOrigin(Align.top);
            //setColor(Color.BLUE);
        } else if (going == Going.LEFT) {
            is_horizontal = true;
            setOrigin(Align.right);
            //setColor(Color.OLIVE);
        }

        // color
        float hue_shift_amount = 360f * -0.32f;
        Color shifted_colour = GameUtils.hueShiftedColor(area.getColor(), hue_shift_amount);
        setColor(shifted_colour);

        setColor(
            getColor().r * 1.5f,
            getColor().g * 1.5f,
            getColor().b * 1.5f,
            1f
        );
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
        } else {
            isCollisionEnabled = false;
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
