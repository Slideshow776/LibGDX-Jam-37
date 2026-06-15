package no.sandramoen.libgdx37.screens.gameplay;

import static net.dermetfan.gdx.scenes.scene2d.Scene2DUtils.stageToLocalCoordinates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TextraLabel;

import no.sandramoen.libgdx37.actors.Background;
import no.sandramoen.libgdx37.actors.Ball;
import no.sandramoen.libgdx37.actors.Divider;
import no.sandramoen.libgdx37.actors.PlayArea;
import no.sandramoen.libgdx37.actors.particles.EffectBurst;
import no.sandramoen.libgdx37.utils.AssetLoader;
import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private BaseActor overlay;
    private Background background;
    private Array<PlayArea> play_areas;

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

        play_areas = new Array<PlayArea>();
        play_areas.add(new PlayArea(mainStage, 1, 1, 14, 7));

        // add ball
        for (PlayArea area : play_areas)
            for (int i = 0; i < 1; i++) {
                area.spawn_ball();
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

        // play-area test
        for (PlayArea area : play_areas) {
            if (area.getBoundaryPolygon().contains(world_position)) {
                Divider divider_up = new Divider(mainStage, world_position, area, Divider.Going.UP);
                //Divider divider_right = new Divider(mainStage, world_position, area, Divider.Going.RIGHT);
                Divider divider_down = new Divider(mainStage, world_position, area, Divider.Going.DOWN);
                //Divider divider_left = new Divider(mainStage, world_position, area, Divider.Going.LEFT);
                area.addActor(divider_up);
                //area.addActor(divider_right);
                area.addActor(divider_down);
                //area.addActor(divider_left);

                PlayArea area_1 = new PlayArea(mainStage, 1, 1, 6, 7);
                PlayArea area_2 = new PlayArea(mainStage, 8, 1, 6, 7);
                play_areas.add(area_1);
                play_areas.add(area_2);

                Array<Ball> balls = area.get_balls();
                for (Ball ball : balls) {
                    Vector2 ball_world_position = ball.localToStageCoordinates(new Vector2());
                    if (area_1.getBoundaryPolygon().contains(ball_world_position)) {
                        area_1.add_ball(ball);
                    } else if (area_2.getBoundaryPolygon().contains(ball_world_position)) {
                        area_2.add_ball(ball);
                    } else {
                        System.out.println("Ball lost!?");
                        ball.remove();
                    }
                }

                play_areas.removeValue(area, false);
                area.remove();
                break;
            }
        }

        // divider test
        /*for (PlayArea area : play_areas) {
            if (area.getBoundaryPolygon().contains(world_position)) {
                Divider divider_up = new Divider(mainStage, world_position, area, Divider.Going.UP);
                //Divider divider_right = new Divider(mainStage, world_position, area, Divider.Going.RIGHT);
                Divider divider_down = new Divider(mainStage, world_position, area, Divider.Going.DOWN);
                //Divider divider_left = new Divider(mainStage, world_position, area, Divider.Going.LEFT);
                area.addActor(divider_up);
                //area.addActor(divider_right);
                area.addActor(divider_down);
                //area.addActor(divider_left);
            }
        }*/

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
