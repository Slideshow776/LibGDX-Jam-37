package no.sandramoen.libgdx37.screens.gameplay;

import static net.dermetfan.gdx.scenes.scene2d.Scene2DUtils.stageToLocalCoordinates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
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
import no.sandramoen.libgdx37.gui.BaseProgressBar;
import no.sandramoen.libgdx37.utils.AssetLoader;
import no.sandramoen.libgdx37.utils.BaseActor;
import no.sandramoen.libgdx37.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private BaseActor overlay;
    private Background background;
    private Array<PlayArea> play_areas;
    private Array<Divider> dividers;

    private boolean is_game_over = false;
    private boolean is_division_horizontal = false;
    private float life_increment = 0f;
    private float life_frequency = 1f;

    private TextraLabel score_label;
    private BaseProgressBar life_bar;
    private BaseProgressBar fulfillment_bar;

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
        play_areas.add(new PlayArea(mainStage, 1, 1.25f, 14, 7));
        for (PlayArea area : play_areas)
            for (int i = 0; i < 8; i++) {
                area.spawn_ball();
            }

        dividers = new Array<Divider>();

        set_vertical_cursor();

        initialize_gui();
    }


    @Override
    public void update(float delta) {
        if (is_game_over)
            return;

        if (life_increment >= life_frequency) {
            life_increment = 0f;
            life_bar.decrementPercentage(1, 2f);
        } else {
            life_increment += delta;
        }


        if (dividers.size == 1) {
            for (Divider divider : dividers) {
                divider.remove();
                dividers.clear();
            }
            return;
        }

        boolean is_both_stopped = true;
        for (Divider divider : dividers) {
            if (divider.is_growing)
                is_both_stopped = false;

            /*for (PlayArea area : play_areas) {
                for (Ball ball : area.get_balls()) {
                    if (ball.overlaps(divider)) {
                        dividers.removeValue(divider, false);
                        divider.remove();
                        area.get_balls().removeValue(ball, false);
                        ball.remove_lost();
                        break;
                    }
                }
            }*/
        }

        if (is_both_stopped && dividers.size == 2) {
            PlayArea area = null;
            boolean is_horizontal = false;
            float divider_x = 0f;
            float divider_y = 0f;
            for (Divider divider : dividers) {
                area = (PlayArea) divider.getParent();
                is_horizontal = divider.is_horizontal;
                divider_x = divider.getX();
                divider_y = divider.getY();
                divider.remove();
            }
            dividers.clear();

            if (is_horizontal)
                split_area_horizontally(area, divider_y);
            else
                split_area_vertically(area, divider_x);
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

        // particle effect
        EffectBurst effect = new EffectBurst();
        effect.setPosition(world_position.x, world_position.y);
        effect.setScale(0.00125f);
        mainStage.addActor(effect);
        effect.start();

        if (button == Input.Buttons.RIGHT) {
            is_division_horizontal = !is_division_horizontal;
            if (is_division_horizontal)
                set_horizontal_cursor();
            else
                set_vertical_cursor();
            return super.touchDown(screenX, screenY, pointer, button);
        }

        // dividers
        for (PlayArea area : play_areas) {
            if (area.contains(world_position) && dividers.size == 0) {
                create_dividers(world_position, area);
                break;
            }
        }

        return super.touchDown(screenX, screenY, pointer, button);
    }


    private void create_dividers(Vector2 world_position, PlayArea area) {
        if (is_division_horizontal) {
            Divider divider_right = new Divider(mainStage, world_position, area, Divider.Going.RIGHT);
            Divider divider_left = new Divider(mainStage, world_position, area, Divider.Going.LEFT);
            area.addActor(divider_right);
            area.addActor(divider_left);
            dividers.add(divider_right);
            dividers.add(divider_left);
        } else {
            Divider divider_up = new Divider(mainStage, world_position, area, Divider.Going.UP);
            Divider divider_down = new Divider(mainStage, world_position, area, Divider.Going.DOWN);
            area.addActor(divider_up);
            area.addActor(divider_down);
            dividers.add(divider_up);
            dividers.add(divider_down);
        }
    }


    private void split_area_horizontally(PlayArea area, float divider_Y) {
        // create areas
        PlayArea area_left = new PlayArea(
            mainStage,
            area.getX(),
            area.getY(),
            area.getWidth(),
            divider_Y
        );
        if (area_left.getWidth() >= Divider.SIZE)
            play_areas.add(area_left);
        else
            area_left.remove();

        PlayArea area_right = new PlayArea(
            mainStage,
            area.getX(),
            area.getY() + divider_Y + Divider.SIZE,
            area.getWidth(),
            area.getHeight() - divider_Y - Divider.SIZE
        );
        if (area_right.getWidth() >= Divider.SIZE)
            play_areas.add(area_right);
        else
            area_right.remove();

        //  transfer balls
        for (Ball ball : area.get_balls()) {
            Vector2 ball_world_position = ball.localToStageCoordinates(new Vector2());

            if (area_left.contains(ball_world_position)) {
                area_left.add_ball(ball);
            } else if (area_right.contains(ball_world_position)) {
                area_right.add_ball(ball);
            } else {
                Ball temp = new Ball(mainStage, ball_world_position.x, ball_world_position.y);
                ball.remove();
                temp.remove_lost();
            }
        }

        play_areas.removeValue(area, false);
        area.remove_split();
    }


    private void split_area_vertically(PlayArea area, float divider_x) {
        // create areas
        PlayArea area_left = new PlayArea(
            mainStage,
            area.getX(),
            area.getY(),
            divider_x,
            area.getHeight()
        );
        if (area_left.getWidth() >= Divider.SIZE)
            play_areas.add(area_left);
        else
            area_left.remove();

        PlayArea area_right = new PlayArea(
            mainStage,
            area.getX() + divider_x + Divider.SIZE,
            area.getY(),
            area.getWidth() - divider_x - Divider.SIZE,
            area.getHeight()
        );
        if (area_right.getWidth() >= Divider.SIZE)
            play_areas.add(area_right);
        else
            area_right.remove();

        //  transfer balls
        for (Ball ball : area.get_balls()) {
            Vector2 ball_world_position = ball.localToStageCoordinates(new Vector2());

            if (area_left.contains(ball_world_position)) {
                area_left.add_ball(ball);
            } else if (area_right.contains(ball_world_position)) {
                area_right.add_ball(ball);
            } else {
                Ball temp = new Ball(mainStage, ball_world_position.x, ball_world_position.y);
                ball.remove();
                temp.remove_lost();
            }
        }

        play_areas.removeValue(area, false);
        area.remove_split();
    }


    private void set_horizontal_cursor() {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("images/excluded/cursor_horizontally.png"));
        // Set hotspot to the middle of it (0,0 would be the top-left corner)
        int xHotspot = 15, yHotspot = 15;
        Cursor cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
        pixmap.dispose(); // We don't need the pixmap anymore
        Gdx.graphics.setCursor(cursor);
    }


    private void set_vertical_cursor() {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("images/excluded/cursor_vertically.png"));
        // Set hotspot to the middle of it (0,0 would be the top-left corner)
        int xHotspot = 15, yHotspot = 15;
        Cursor cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
        pixmap.dispose(); // We don't need the pixmap anymore
        Gdx.graphics.setCursor(cursor);
    }


    private void initialize_gui() {
        // resources setup
        life_bar = new BaseProgressBar(Gdx.graphics.getWidth() * .0325f, Gdx.graphics.getHeight() * 0.9725f, uiStage);
        life_bar.setProgress(100);
        life_bar.set_color(Color.FIREBRICK);
        life_bar.setProgressBarColor(Color.PINK);
        uiStage.addActor(life_bar);

        fulfillment_bar = new BaseProgressBar(Gdx.graphics.getWidth() * .0325f, Gdx.graphics.getHeight() * 0.0555f, uiStage);
        fulfillment_bar.setProgress(0);
        fulfillment_bar.set_color(Color.BROWN);
        fulfillment_bar.setProgressBarColor(Color.GOLD);
        uiStage.addActor(fulfillment_bar);

        // ui setup
        uiTable.defaults()
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;

        uiTable.add()
            .padTop(Gdx.graphics.getHeight() * .1f)
            .row()
        ;

        //uiTable.setDebug(true);
    }
}
