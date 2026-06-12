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

import no.sandramoen.libgdx37.actors.Bridge;
import no.sandramoen.libgdx37.actors.Grass;
import no.sandramoen.libgdx37.actors.Overlay;
import no.sandramoen.libgdx37.actors.Player;
import no.sandramoen.libgdx37.actors.Rock;
import no.sandramoen.libgdx37.actors.Sheep;
import no.sandramoen.libgdx37.actors.Water;
import no.sandramoen.libgdx37.actors.WinArea;
import no.sandramoen.libgdx37.actors.particles.EffectBurst;
import no.sandramoen.libgdx37.utils.AssetLoader;
import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseGame;
import no.sandramoen.libgdx37.utils.BaseScreen;

import com.badlogic.gdx.utils.QuadTreeFloat;

public class LevelScreen extends BaseScreen {

    private BaseActor overlay;
    private Grass grass;
    private Water water;
    private Array<Bridge> bridges;
    private Player player;
    private Array<Sheep> sheep;
    private Array<Rock> rocks;
    private WinArea winArea;

    private boolean is_game_over = false;
    private final int NUM_SHEEP = 480;
    private int sheep_killed = 0;
    private int sheep_herded = 0;
    private int score = 0;

    private TextraLabel score_label;
    private TextraLabel kill_label;

    private QuadTreeFloat quad;

    public LevelScreen() {}


    @Override
    public void initialize() {
        // audio
        BaseGame.soundVolume = 1.0f;
        BaseGame.musicVolume = 1.0f;

        AssetLoader.ambianceMusic.setLooping(true);
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
        AssetLoader.herdMusic.play();

        // actors
        grass = new Grass(mainStage);

        water = new Water(
            new Vector2(-1, 11.5f),
            new Vector2(BaseGame.WORLD_WIDTH * 1.2f, 3),
            mainStage
        );

        bridges = new Array<Bridge>();
        bridges.add(new Bridge(
            new Vector2(3, 11f),
            new Vector2(2, 4),
            mainStage,
            1
        ));
        bridges.add(new Bridge(
            new Vector2(12, 11f),
            new Vector2(1, 4),
            mainStage,
            4
        ));

        quad = new QuadTreeFloat(32, 6);
        quad.setBounds(0, 0, BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT);

        rocks = new Array<Rock>();
        /*rocks.add(new Rock(8f, 2.5f, mainStage));
        rocks.add(new Rock(4f, 5f, mainStage));*/

        sheep = new Array<Sheep>();
        for (int i = 0; i < NUM_SHEEP / 4; i++) sheep.add(new Sheep(12, 7.5f, mainStage));
        for (int i = 0; i < NUM_SHEEP / 4; i++) sheep.add(new Sheep(10, 2f, mainStage));
        for (int i = 0; i < NUM_SHEEP / 4; i++) sheep.add(new Sheep(3, 4f, mainStage));
        for (int i = 0; i < NUM_SHEEP / 4; i++) sheep.add(new Sheep(2, 1.25f, mainStage));

        for (int i = 0, n = sheep.size; i < n; i++) {
            Sheep s = sheep.get(i);
            quad.add(i, s.getX(Align.center), s.getY(Align.center));
        }

        player = new Player(new Vector2(14, 2), mainStage);

        initialize_gui();
        //GameUtils.playLoopingMusic(AssetLoader.levelMusic);

        winArea = new WinArea(new Vector2(0, BaseGame.WORLD_HEIGHT - 0.5f), new Vector2(BaseGame.WORLD_WIDTH, 1f), mainStage);
        overlay = new Overlay(mainStage);
    }


    @Override
    public void update(float delta) {
        System.out.println("LevelScreen.java => levelMusic volume = " + AssetLoader.levelMusic.getVolume());
        if (!is_game_over && AssetLoader.levelMusic.getVolume() >= 0.25f) // lower music volume on start
            AssetLoader.levelMusic.setVolume(AssetLoader.levelMusic.getVolume() - 0.00015f);
        else if (is_game_over && AssetLoader.levelMusic.getVolume() <= BaseGame.musicVolume) { // raise music volume on end
            AssetLoader.levelMusic.setVolume(AssetLoader.levelMusic.getVolume() + 0.00015f);
            AssetLoader.levelMusic.setLooping(false);
        }

        update_sheep();

        // safe removal of sheep
        // allows faster removal when we can safely rearrange sheep indices.
        sheep.ordered = false;
        for (int i = sheep.size - 1; i >= 0; i--) {
            if (sheep.get(i).is_ready_to_be_removed) {
                sheep.get(i).remove();
                sheep.removeIndex(i);
            }
        }
        // must set ordered back to true when we're done deleting.
        sheep.ordered = true;
        quad.reset();
        for (int i = 0, n = sheep.size; i < n; i++) {
            Sheep s = sheep.get(i);
            quad.add(i, s.getX(Align.center), s.getY(Align.center));
        }

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

        if (MathUtils.random() > 0.8f)
            AssetLoader.dogSounds.get(MathUtils.random(0, AssetLoader.dogSounds.size - 1)).play(BaseGame.soundVolume, MathUtils.random(0.9f, 1.1f), 0f);

        EffectBurst effect = new EffectBurst();
        effect.setPosition(world_position.x, world_position.y);
        effect.setScale(0.00125f);
        mainStage.addActor(effect);
        effect.start();

        return super.touchDown(screenX, screenY, pointer, button);
    }


    private void update_sheep() {
        // set avoidance behaviour
        float player_distance = 100f;
        // collision checks
        for (int i = 0; i < sheep.size; i++) {
            Sheep s = sheep.get(i);
            // guard rails
            s.updateBehaviour(player.getX(), player.getY(), sheep, quad);

            player_distance = Math.min(player_distance, Vector2.dst(s.getX(), s.getY(), player.getX(), player.getY()));

            // collision checks
            /*for (Rock rock : rocks) {
                s.preventOverlap(rock);
            }*/

            boolean is_on_a_bridge = false;
            for (Bridge bridge : bridges) {
                // left guard-rail
                Vector2 temp = s.preventOverlap(bridge.left_rail);
                if (temp != null) s.accelerateAtAngle(temp.angleDeg());

                // right guard-rail
                temp = s.preventOverlap(bridge.right_rail);
                if (temp != null) s.accelerateAtAngle(temp.angleDeg());

                if (s.overlaps(bridge)) {
                    is_on_a_bridge = true;
                    s.scoreMultiplier = bridge.multiplier;
                }

            }

            if (s.overlaps(water) && !is_on_a_bridge) { // death check
                s.die();
                sheep_killed++;
                AssetLoader.waterSound.play(BaseGame.soundVolume, MathUtils.random(0.8f, 1.2f), 0f);
                kill_label.setText(String.valueOf(sheep_killed));
            }

            if (s.overlaps(winArea)) { // win check
                s.herded();
                sheep_herded++;
                score += s.scoreMultiplier;
                AssetLoader.sheepSounds.get(MathUtils.random(0, AssetLoader.sheepSounds.size - 1)).play(BaseGame.soundVolume * 0.5f, MathUtils.random(0.6f, 1.4f), 0f);
                score_label.setText(String.valueOf(score));
                checkWinCondition();
            }
        }

        if (player_distance < 100) {
            float volume = MathUtils.clamp(BaseGame.soundVolume * (0.5f / player_distance * 0.7f),0f, BaseGame.soundVolume);
            AssetLoader.herdMusic.setVolume(volume);
        } else {
            AssetLoader.herdMusic.setVolume(0f);
        }
    }


    private void checkWinCondition() {
        //System.out.println(sheep.size + ", " + MathUtils.ceil(NUM_SHEEP * 0.02f));
        if (sheep.size >= MathUtils.ceil(NUM_SHEEP * 0.02f))
            return;

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

        Image kill_image = new Image(AssetLoader.textureAtlas.findRegion("skull"));
        kill_label = new TextraLabel("0", AssetLoader.getLabelStyle("IrishGrover_59"));
        kill_label.getFont().scale(label_scale);
        kill_label.setColor(Color.FIREBRICK);
        kill_label.setAlignment(Align.center);

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

        Table kill_table = new Table();
        kill_table.add(kill_image)
            .width(Gdx.graphics.getWidth() * 0.025f)
            .height(Gdx.graphics.getHeight() * 0.04f)
        ;
        kill_table.add(kill_label)
            .top()
            .padLeft(Gdx.graphics.getWidth() * 0.01f)
        ;

        uiTable.add(kill_table)
            .expandY()
            .top()
        ;

        //uiTable.setDebug(true);
    }
}
