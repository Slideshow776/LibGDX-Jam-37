package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.libgdx37.utils.BaseActor;

public class WinArea extends BaseActor {
    public WinArea(Vector2 position, Vector2 size, Stage stage) {
        super(position.x, position.y, stage);
        setSize(size.x, size.y);
        setBoundaryRectangle(1f);
    }
}
