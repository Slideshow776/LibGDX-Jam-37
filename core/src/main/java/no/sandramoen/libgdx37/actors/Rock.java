package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx37.utils.BaseActor;

public class Rock extends BaseActor {
    public Rock(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("whitePixel");
        setColor(new Color(0x9eb2b2FF));

        // body
        setSize(
            1f,
            1f
        );
        centerAtPosition(x, y);
        setOrigin(Align.center);
        setBoundaryRectangle(0.9f);
    }
}
