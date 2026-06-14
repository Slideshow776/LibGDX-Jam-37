package no.sandramoen.libgdx37.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import no.sandramoen.libgdx37.actors.Background;
import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseScreen;


public class MenuScreen extends BaseScreen {

    private Background background;
    private BaseActor overlay;

    @Override
    public void initialize() {}


    @Override
    public void update(float delta) {}


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q) {
            Gdx.app.exit();
        }
        return super.keyDown(keycode);
    }
}
