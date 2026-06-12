package no.sandramoen.libgdx37.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.tommyettinger.textra.TextraLabel;

import no.sandramoen.libgdx37.utils.AssetLoader;
import no.sandramoen.libgdx37.utils.BaseActor;

public class Bridge extends BaseActor {
    public BaseActor left_rail;
    public BaseActor right_rail;
    public int multiplier;

    public Bridge(Vector2 position, Vector2 size, Stage stage, int multiplier) {
        super(position.x, position.y, stage);
        this.multiplier = multiplier;

        loadImage("whitePixel");
        setSize(size.x, size.y);
        setColor(new Color(0xdbb88dFF));

        setBoundaryRectangle(1f);

        // guard rails
        left_rail = new BaseActor(getX(), getY(), stage);
        left_rail.loadImage("whitePixel");
        left_rail.setColor(new Color(0x987b57FF));
        left_rail.setSize(size.x * 0.1f, size.y);
        left_rail.setBoundaryRectangle(1f);
        stage.addActor(left_rail);

        right_rail = new BaseActor(0, 0, stage);
        right_rail.loadImage("whitePixel");
        right_rail.setColor(new Color(0x987b57FF));
        right_rail.setSize(size.x * 0.1f, size.y);
        right_rail.setPosition(getX() + size.x - right_rail.getWidth(), getY());
        right_rail.setBoundaryRectangle(1f);
        stage.addActor(right_rail);

        TextraLabel label = new TextraLabel(multiplier + "x", AssetLoader.getLabelStyle("IrishGrover_20"));
        label.setSize(1f, 1f);
        label.setColor(new Color(0x6c4b3aFF));
        label.getFont().scale(0.005f);
        label.setPosition(
            getWidth() / 2 - 0.175f,
            getHeight() / 2 - 0.5f
        );
        addActor(label);
    }
}
