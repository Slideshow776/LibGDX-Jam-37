package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.libgdx37.utils.BaseActor;

public class Water extends BaseActor {
    public Water(Vector2 position, Vector2 size, Stage stage) {
        super(position.x, position.y, stage);

        loadImage("whitePixel");

        setSize(size.x, size.y);
        setBoundaryRectangle(1f);

        setColor(new Color(0x266b82FF));
    }
}
