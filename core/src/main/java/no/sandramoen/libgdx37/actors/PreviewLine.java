package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.GameUtils;

public class PreviewLine extends BaseActor {

    public static final float SIZE = 1f;

    public enum Going {
         UP,
         RIGHT,
         DOWN,
         LEFT
    }
    public boolean is_horizontal = false;
    public float ORIGINAL_OPACITY = 0f;

    private Going going;
    private PlayArea area;

    public PreviewLine(Stage stage, Vector2 position, PlayArea area, Going going) {
        super(position.x, position.y, stage);

        this.area = area;

        loadImage("whitePixel");
        setTouchable(Touchable.disabled);

        // body
        setOrigin(Align.center);
        setBoundaryRectangle(1f);

        Vector2 divider_world_position = localToStageCoordinates(new Vector2());
        Vector2 new_local_position = area.stageToLocalCoordinates(divider_world_position);
        setPosition(
            new_local_position.x - getWidth() * 3f,
            new_local_position.y - getHeight() * 3f,
            Align.center
        );

        //setDebug(true);
        this.going = going;

        setWorldBounds(area);

        // colour
        float hue_shift_amount = 360f * 0.32f;
        Color shifted_colour = GameUtils.hueShiftedColor(area.getColor(), hue_shift_amount);

        ORIGINAL_OPACITY = 0.5f;
        shifted_colour.set(
            shifted_colour.r * 0.25f,
            shifted_colour.g * 0.25f,
            shifted_colour.b * 0.25f,
            ORIGINAL_OPACITY
        );

        setColor(shifted_colour);
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        if (area.is_being_divided)
            setOpacity(0f);

        if (going == Going.UP) {
            setSize(Divider.SIZE, area.getHeight());
            setX(MathUtils.clamp(getX(), 0, area.getWidth()));
            setY(0);
//            setOrigin(Align.bottom);
        } else if (going == Going.RIGHT) {
            is_horizontal = true;
            setSize(area.getWidth(), Divider.SIZE);
            setX(0);
            setY(MathUtils.clamp(getY(), 0, area.getHeight()));
//            setOrigin(Align.left);
        } else if (going == Going.DOWN) {
            setSize(Divider.SIZE, area.getHeight());
            setX(MathUtils.clamp(getX(), 0, area.getWidth()));
            setY(0);
//            setOrigin(Align.bottom);
        } else if (going == Going.LEFT) {
            is_horizontal = true;
            setSize(area.getWidth(), Divider.SIZE);
            setX(0);
            setY(MathUtils.clamp(getY(), 0, area.getHeight()));
//            setOrigin(Align.left);
        }
        setWorldBounds(area);
    }
}
