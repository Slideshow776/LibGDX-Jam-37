package no.sandramoen.libgdx37.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TextraLabel;

import no.sandramoen.libgdx37.actors.Background;
import no.sandramoen.libgdx37.actors.Ball;
import no.sandramoen.libgdx37.actors.PlayArea;
import no.sandramoen.libgdx37.actors.particles.EffectBurst;
import no.sandramoen.libgdx37.utils.AssetLoader;
import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private BaseActor overlay;
    private Background background;
    private PlayArea playArea;

    private boolean is_game_over = false;
    private int score = 0;

    private TextraLabel score_label;

    public LevelScreen() {}


    @Override
    public void initialize() {
        // audio

        /*AssetLoader.ambianceMusic.setLooping(true);
        AssetLoader.ambianceMusic.setPosition(MathUtils.random(0f, 40f));
        AssetLoader.ambianceMusic.setVolume(BaseGame.musicVolume);
        AssetLoader.ambianceMusic.play();*/

        // actors
        background = new Background(mainStage);
        playArea = new PlayArea(mainStage);

        // todo add ball
        for (int i = 0; i < 4; i++) {
            Ball ball = new Ball(mainStage);
            playArea.addActor(ball);
            ball.setWorldBounds(playArea.getWidth(), playArea.getHeight());
            ball.setPosition(
                MathUtils.random(0f, playArea.getWidth()),
                MathUtils.random(0f, playArea.getHeight())
            );
        }
    }


    @Override
    public void update(float delta) {}


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.Q) {
            Gdx.app.exit();
        }
        return super.keyDown(keycode);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 world_position = mainStage.screenToStageCoordinates(new Vector2(screenX, screenY));

        EffectBurst effect = new EffectBurst();
        effect.setPosition(world_position.x, world_position.y);
        effect.setScale(0.00125f);
        mainStage.addActor(effect);
        effect.start();

        return super.touchDown(screenX, screenY, pointer, button);
    }


    private void initialize_gui() {
        // resources setup
        float label_scale = 0.5f;
        Image score_image = new Image(AssetLoader.textureAtlas.findRegion("trophy"));
        score_label = new TextraLabel("0", AssetLoader.getLabelStyle("IrishGrover_59"));
        score_label.getFont().scale(label_scale);
        score_label.setColor(Color.FOREST);
        score_label.setAlignment(Align.center);


        // ui setup
        uiTable.defaults()
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;

        Table table = new Table();
        table.add(score_image)
            .width(Gdx.graphics.getWidth() * 0.025f)
            .height(Gdx.graphics.getHeight() * 0.04f)
        ;
        table.add(score_label)
            .top()
            .padLeft(Gdx.graphics.getWidth() * 0.01f)
        ;

        uiTable.add(table)
            .padTop(Gdx.graphics.getHeight() * .1f)
            .row()
        ;


        //uiTable.setDebug(true);
    }
}
