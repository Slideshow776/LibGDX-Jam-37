package no.sandramoen.libgdx37.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TextraLabel;

import no.sandramoen.libgdx37.actors.Overlay;
import no.sandramoen.libgdx37.actors.Player;
import no.sandramoen.libgdx37.actors.particles.EffectBurst;
import no.sandramoen.libgdx37.utils.AssetLoader;
import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseGame;
import no.sandramoen.libgdx37.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private BaseActor overlay;
    private Player player;

    private boolean is_game_over = false;
    private int score = 0;

    private TextraLabel score_label;

    public LevelScreen() {}


    @Override
    public void initialize() {
        // audio
        BaseGame.soundVolume = 1.0f;
        BaseGame.musicVolume = 1.0f;

        /*AssetLoader.ambianceMusic.setLooping(true);
        AssetLoader.ambianceMusic.setPosition(MathUtils.random(0f, 40f));
        AssetLoader.ambianceMusic.setVolume(BaseGame.musicVolume);
        AssetLoader.ambianceMusic.play();

        AssetLoader.levelMusic.setLooping(true);
        AssetLoader.levelMusic.setPosition(MathUtils.random(0f, 40f));
        AssetLoader.levelMusic.setVolume(BaseGame.musicVolume);
        AssetLoader.levelMusic.play();

        AssetLoader.herdMusic.setLooping(true);
        AssetLoader.herdMusic.setPosition(MathUtils.random(0f, 40f));
        AssetLoader.herdMusic.setVolume(0f);
        AssetLoader.herdMusic.play();*/

        // actors
        player = new Player(new Vector2(14, 2), mainStage);

        initialize_gui();
        //GameUtils.playLoopingMusic(AssetLoader.levelMusic);

        overlay = new Overlay(mainStage);
    }


    @Override
    public void update(float delta) {
    }


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
        player.touch_position = world_position;

        /*if (MathUtils.random() > 0.8f)
            AssetLoader.dogSounds.get(MathUtils.random(0, AssetLoader.dogSounds.size - 1)).play(BaseGame.soundVolume, MathUtils.random(0.9f, 1.1f), 0f);*/

        EffectBurst effect = new EffectBurst();
        effect.setPosition(world_position.x, world_position.y);
        effect.setScale(0.00125f);
        mainStage.addActor(effect);
        effect.start();

        return super.touchDown(screenX, screenY, pointer, button);
    }



    private void checkWinCondition() {
        is_game_over = true;
        System.out.println("a winner is you!");
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

        Table herd_table = new Table();
        herd_table.add(score_image)
            .width(Gdx.graphics.getWidth() * 0.025f)
            .height(Gdx.graphics.getHeight() * 0.04f)
        ;
        herd_table.add(score_label)
            .top()
            .padLeft(Gdx.graphics.getWidth() * 0.01f)
        ;

        uiTable.add(herd_table)
            .padTop(Gdx.graphics.getHeight() * .1f)
            .row()
        ;

        //uiTable.setDebug(true);
    }
}
