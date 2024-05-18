package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Null;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.views.GameCanvas;

public abstract class CharactersModel extends GameObject {
    protected Vector2 velocity;
    float radius;
    protected FilmStrip run_left;
    protected FilmStrip run_right;
    protected FilmStrip run_down;
    protected FilmStrip run_up;
    protected FilmStrip idleright;
    protected FilmStrip idleleft;
    protected FilmStrip idleup;
    protected FilmStrip win;
    protected FilmStrip death;
    protected FilmStrip vacuum_start_left;
    protected FilmStrip vacuum_left;
    protected FilmStrip vacuum_end_left;
    protected FilmStrip vacuum_start_right;
    protected FilmStrip vacuum_right;
    protected FilmStrip vacuum_end_right;
    protected String type;

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(float x, float y) {
        velocity = new Vector2(x, y);
    }

    public void setVelocity(Vector2 v) {
        velocity = new Vector2(v);
    }

    public float getRadius() {
        return this.radius;
    }

    public FilmStrip getFilmStrip(String type) {
        switch (type) {
            case "left":
                return run_left;
            case "right":
                return run_right;
            case "up":
                return run_up;
            case "down":
                return run_down;
            case "idleright":
                return idleright;
            case "idleleft":
                return idleleft;
            case "idleup":
                return idleup;
            case "death":
                return death;
            case "win":
                return win;
            case "vacuumstartleft":
                return vacuum_start_left;
            case "vacuumleft":
                return vacuum_left;
            case "vacuumendleft":
                return vacuum_end_left;
            case "vacuumstartright":
                return vacuum_start_right;
            case "vacuumright":
                return vacuum_right;
            case "vacuumendright":
                return vacuum_end_right;
            case "vacuumstartup":
                return vacuum_start_right;
            case "vacuumup":
                return vacuum_right;
            case "vacuumendup":
                return vacuum_end_right;
            default:
                return null;
        }
    }

    public void resetFilmStrip(FilmStrip value) {
        value.setFrame(0);
    }

    public void drawCharacter(GameCanvas canvas, float rotation, Color tint, String state, String direction) {
        String directiontrue = "none";
        if (this instanceof EnemyModel) {
            directiontrue = getDirection(this.getVelocity().x, this.getVelocity().y, "none");
        }
        switch (state) {
            case "idle":
                switch (direction) {
                    case "left":
                        canvas.draw(idleleft, tint, (float) idleleft.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    case "right":
                        canvas.draw(idleright, tint, (float) idleright.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    default:
                        canvas.draw(idleup, tint, (float) idleup.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                }
                break;
            case "running":
                if (this instanceof EnemyModel) {
                    direction = directiontrue;
                }
                switch (direction) {
                    case "left":
                        canvas.draw(run_left, tint, (float) run_left.getRegionWidth() / 2, 140, position.x, position.y, 0, .25f, .25f, type.equals("enemy"));
                        break;
                    case "right":
                        canvas.draw(run_right, tint, (float) run_right.getRegionWidth() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    case "up":
                        canvas.draw(run_up, tint, (float) run_up.getRegionWidth() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    case "down":
                        canvas.draw(run_down, tint, (float) run_down.getRegionWidth() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    default:
                        throw new IllegalArgumentException("Character animation fail");
                }
                break;
            case "death":
                switch (direction) {
                    case "right":
                        canvas.draw(death, tint, (float) death.getRegionWidth() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    case "left":
                        canvas.draw(death, tint, (float) death.getRegionWidth() / 2, 140, position.x, position.y, 0, .25f, .25f, true);
                        break;
                    default:
                        canvas.draw(death, tint, (float) death.getRegionWidth() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                }
                break;
            case "win":
                switch (direction) {
                    case "right":
                        canvas.draw(win, tint, (float) win.getRegionWidth() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    case "left":
                        canvas.draw(win, tint, (float) win.getRegionWidth() / 2, 140, position.x, position.y, 0, .25f, .25f, true);
                        break;
                    default:
                        canvas.draw(win, tint, (float) win.getRegionWidth() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                }
                break;
            case "vacuuming_start":
                switch (direction) {
                    case "left":
                        canvas.draw(vacuum_start_left, tint, (float) idleleft.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    case "right":
                        canvas.draw(vacuum_start_right, tint, (float) idleright.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    default:
                        canvas.draw(vacuum_start_left, tint, (float) idleup.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                }
                break;
            case "vacuuming":
                switch (direction) {
                    case "left":
                        canvas.draw(vacuum_left, tint, (float) idleleft.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    case "right":
                        canvas.draw(vacuum_right, tint, (float) idleright.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    default:
                        canvas.draw(vacuum_left, tint, (float) idleup.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                }
                break;
            case "vacuuming_end":
                switch (direction) {
                    case "left":
                        canvas.draw(vacuum_end_left, tint, (float) idleleft.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    case "right":
                        canvas.draw(vacuum_end_right, tint, (float) idleright.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                        break;
                    default:
                        canvas.draw(vacuum_end_left, tint, (float) idleup.getRegionHeight() / 2, 140, position.x, position.y, 0, .25f, .25f, false);
                }
                break;
            default:
                break;
        }


    }

    public void drawCharacter(GameCanvas canvas, float scale, float rotation, Color tint, String state, String direction) {
        String directiontrue = "none";
        if (this instanceof EnemyModel) {
            directiontrue = getDirection(this.getVelocity().x, this.getVelocity().y, "none");
        }
        switch (state) {
            case "idle":
                switch (direction) {
                    case "left":
                        canvas.draw(idleleft, tint, (float) idleleft.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    case "right":
                        canvas.draw(idleright, tint, (float) idleright.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    default:
                        canvas.draw(idleup, tint, (float) idleup.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                }
                break;
            case "running":
                if (this instanceof EnemyModel) {
                    direction = directiontrue;
                }
                switch (direction) {
                    case "left":
                        canvas.draw(run_left, tint, (float) run_left.getRegionWidth() / 2, 140, position.x, position.y, 0, scale, scale, type.equals("enemy"));
                        break;
                    case "right":
                        canvas.draw(run_right, tint, (float) run_right.getRegionWidth() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    case "up":
                        canvas.draw(run_up, tint, (float) run_up.getRegionWidth() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    case "down":
                        canvas.draw(run_down, tint, (float) run_down.getRegionWidth() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    default:
                        throw new IllegalArgumentException("Character animation fail");
                }
                break;
            case "death":
                switch (direction) {
                    case "right":
                        canvas.draw(death, tint, (float) death.getRegionWidth() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    case "left":
                        canvas.draw(death, tint, (float) death.getRegionWidth() / 2, 140, position.x, position.y, 0, scale, scale, true);
                        break;
                    default:
                        canvas.draw(death, tint, (float) death.getRegionWidth() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                }
                break;
            case "win":
                switch (direction) {
                    case "right":
                        canvas.draw(win, tint, (float) win.getRegionWidth() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    case "left":
                        canvas.draw(win, tint, (float) win.getRegionWidth() / 2, 140, position.x, position.y, 0, scale, scale, true);
                        break;
                    default:
                        canvas.draw(win, tint, (float) win.getRegionWidth() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                }
                break;
            case "vacuuming_start":
                switch (direction) {
                    case "left":
                        canvas.draw(vacuum_start_left, tint, (float) idleleft.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    case "right":
                        canvas.draw(vacuum_start_right, tint, (float) idleright.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    default:
                        canvas.draw(vacuum_start_left, tint, (float) idleup.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                }
                break;
            case "vacuuming":
                switch (direction) {
                    case "left":
                        canvas.draw(vacuum_left, tint, (float) idleleft.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    case "right":
                        canvas.draw(vacuum_right, tint, (float) idleright.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    default:
                        canvas.draw(vacuum_left, tint, (float) idleup.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                }
                break;
            case "vacuuming_end":
                switch (direction) {
                    case "left":
                        canvas.draw(vacuum_end_left, tint, (float) idleleft.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    case "right":
                        canvas.draw(vacuum_end_right, tint, (float) idleright.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                        break;
                    default:
                        canvas.draw(vacuum_end_left, tint, (float) idleup.getRegionHeight() / 2, 140, position.x, position.y, 0, scale, scale, false);
                }
                break;
            default:
                break;
        }


    }

    public String getDirection(float x, float y, String previous) {
        float angle = (float) Math.toDegrees(Math.atan2(x, y));

        if (this instanceof PlayerModel) {
            if (x == 0 && y == 0) {
                return previous;
            }
            if (angle >= 0 && angle <= 135) {
                return "right";
            } else if (angle >= 135 || angle <= -135) {
                return "up";
            } else if (angle >= -135 && angle <= 0) {
                return "left";
            }
        } else if (this instanceof EnemyModel) { // Assume model instanceof EnemyModel
            angle = (float) Math.toDegrees(Math.atan2(y, x));
            // setRotation is broken as of now, but also have found workaround that doesn't use setRotation
//            model.setRotation(angle);
            if (angle <= 45 && angle >= -45) {
                return "right";
            } else if (angle >= 45 && angle <= 135) {
                return "up";
            } else if (angle >= 135 || angle <= -135) {
                return "left";
            } else {
                return "down";
            }
        }
        return "none";
    }
}
