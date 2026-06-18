package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx37.utils.BaseActor;

public class PreviewLine extends BaseActor {

    public static final float SIZE = 1f;
    public enum Going {
         UP,
         RIGHT,
         DOWN,
         LEFT
    }
    public boolean is_horizontal = false;

    private Going going;

    public PreviewLine(Stage stage, Vector2 position, PlayArea area, Going going) {
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
        this.going = going;
        if (going == Going.UP) {
            setScaleY(area.getHeight());
            setWidth(0.25f);
            setY(0);
            setOrigin(Align.bottom);
        } else if (going == Going.RIGHT) {
            is_horizontal = true;
            setScaleX(area.getWidth());
            setHeight(0.25f);
            setX(0);
            setOrigin(Align.left);
        } else if (going == Going.DOWN) {
            setScaleY(area.getHeight());
            setWidth(0.25f);
            setY(0);
            setOrigin(Align.bottom);
        } else if (going == Going.LEFT) {
            is_horizontal = true;
            setScaleX(area.getWidth());
            setHeight(0.25f);
            setX(0);
            setOrigin(Align.left);
        }
        setWorldBounds(area);
        setColor(0.7f, 0.7f, 0.7f, 0.5f); // transparent light gray
        isCollisionEnabled = false;
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
