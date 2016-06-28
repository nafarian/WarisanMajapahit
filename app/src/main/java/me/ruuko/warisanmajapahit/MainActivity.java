package me.ruuko.warisanmajapahit;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWebView();
        setupToggleVolume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionToggleVolume:
                togglePlay();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder askQuit = new AlertDialog.Builder(this);
        askQuit.setMessage("Apakah Anda yakin ingin keluar dari aplikasi ini?");
        askQuit.setTitle("Keluar Aplikasi");
        askQuit.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        askQuit.setNegativeButton("Tidak", null);
        askQuit.create().show();
    }

    @Override
    public void finish() {
        stopMusic();
        super.finish();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setupWebView() {
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.loadUrl("file:///android_asset/www/index.html");
        class JSIntervace {
            @JavascriptInterface
            public String loadMusic(String musicFile) {
                stopMusic();
                playMusic(musicFile);
                return "";
            }
        }
        webView.addJavascriptInterface(new JSIntervace(), "toJava");
    }

    private void setupToggleVolume() {
        ActionMenuItemView toggleVolume = (ActionMenuItemView) findViewById(R.id.actionToggleVolume);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                toggleVolume.setIcon(getResources().getDrawable(R.drawable.ic_volume_off_white_24dp));
            } else {
                toggleVolume.setIcon(getResources().getDrawable(R.drawable.ic_volume_up_white_24dp));
            }
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playMusic(String musicFile) {
        String fileName = "www/" + musicFile;
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor descriptor = this.getAssets().openFd(fileName);
            long start = descriptor.getStartOffset();
            long end = descriptor.getLength();
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), start, end);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to open music file.", Toast.LENGTH_LONG).show();
        }
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
    }

    private void togglePlay() {
        ActionMenuItemView toggleVolume = (ActionMenuItemView) findViewById(R.id.actionToggleVolume);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                toggleVolume.setIcon(getResources().getDrawable(R.drawable.ic_volume_up_white_24dp));
            } else {
                mediaPlayer.start();
                toggleVolume.setIcon(getResources().getDrawable(R.drawable.ic_volume_off_white_24dp));
            }
        }
    }
}
