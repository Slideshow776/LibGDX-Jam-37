package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseGame;

public class PlayArea extends BaseActor {


    public PlayArea(Stage stage) {
        super(0f, 0f, stage);

        loadImage("whitePixel");
        setColor(Color.GRAY);

        // body
        setSize(14, 7);
        centerAtPosition(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2);
        setOrigin(Align.center);
        setBoundaryRectangle(1f);

        setDebug(true);
    }
}
