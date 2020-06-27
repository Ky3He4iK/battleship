package dev.ky3he4ik.battleship.gui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.utils.Constants;
import dev.ky3he4ik.battleship.utils.vectors.Vec2d;

/**
 * Класс с набором полезных функций для графики
 */
public final class GH {
    /**
     * Возвращает вектор на левый нижний угол объекта @param actor
     */
    @NotNull
    public static Vec2d getAbsCoord(@Nullable Actor actor) {
        Vec2d coord = new Vec2d(0, 0);
        while (actor != null) {
            coord.x += actor.getX();
            coord.y += actor.getY();
            actor = actor.getParent();
        }
        return coord;
    }

    /**
     * Изменяет ширину и высоту объекта @param actor в соответствии с @param width с сохранением отношения сторон
     * и перемещает в место @param x и @param y
     */
    public static void setBoundsByWidth(@NotNull ActorWithSprite actor, float x, float y, float width) {
        float scale = actor.getSprite().getWidth() / width;
        actor.setBounds(x, y, width, actor.getSprite().getHeight() / scale);
    }

    /**
     * Изменяет ширину и высоту объекта @param actor в соответствии с @param height с сохранением отношения сторон
     * и перемещает в место @param x и @param y
     */
    public static void setBoundsByHeight(@NotNull ActorWithSprite actor, float x, float y, float height) {
        float scale = actor.getSprite().getHeight() / height;
        actor.setBounds(x, y, actor.getSprite().getWidth() / scale, height);
    }

    /**
     * Преобразовывает спрайт с именем @param spriteName в SpriteDrawable и устанавливает минимальные размеры
     * в десятую часть дирины и высоты экрана приложения
     */
    @NotNull
    public static SpriteDrawable getSpriteDrawable(@NotNull String spriteName) {
        SpriteDrawable drawable = new SpriteDrawable(SpriteManager.getInstance().getSprite(spriteName));
        drawable.setMinHeight(Gdx.graphics.getHeight() / 10f);
        drawable.setMinWidth(Gdx.graphics.getWidth() / 10f);
        return drawable;
    }

    /**
     * Создает чекбокс с шрифтом @param font
     */
    @NotNull
    public static CheckBox getCheckbox(@NotNull BitmapFont font) {
        return new CheckBox(null, new CheckBox.CheckBoxStyle(getSpriteDrawable(Constants.BUTTON_DONE_FRAME), getSpriteDrawable(Constants.BUTTON_DONE_SELECTED), font, font.getColor()));
    }
}
