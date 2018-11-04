package io.github.liaob.dangowallpaper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.io.InputStream;

public class DangoWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {

        int resource = getColorResource();

        @SuppressLint("ResourceType") Movie movie = Movie.decodeStream(
                getResources().openRawResource(resource));
        return new DangoWallpaperEngine(movie);
    }

    private class DangoWallpaperEngine extends Engine {
        private SurfaceHolder holder;
        private Movie movie;
        private boolean visible;
        private Handler handler;
        private int width;
        private int height;
        private float scalex;
        private float scaley;

        public DangoWallpaperEngine(Movie movie){
            this.movie  = movie;
            handler = new Handler();
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
            width = getResources().getDisplayMetrics().widthPixels;
            height = getResources().getDisplayMetrics().heightPixels;
        }

        private Runnable drawGIF = new Runnable() {
            public void run() {
                draw();
            }
        };

        private void draw() {
            if (visible) {
                Canvas canvas = holder.lockCanvas();
                canvas.save();
                scalex = width / (1f * movie.width());
                scaley = height / (1f*movie.height());
                canvas.scale(scalex, scaley);
                movie.draw(canvas, 0, 0);
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                movie.setTime((int) (System.currentTimeMillis() % movie.duration()));
                handler.removeCallbacks(drawGIF);
                handler.postDelayed(drawGIF, 0);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawGIF);
            } else {
                handler.removeCallbacks(drawGIF);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGIF);
        }
    }

    public int getColorResource(){
        return R.drawable.bluedango;
    }
}

