import android.content.Context;
import android.media.MediaPlayer;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MyMediaPlayer implements MediaPlayer.OnBufferingUpdateListener, SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private MediaPlayer player;

    private SeekBar seekBar;

    private boolean isTracking;

    private boolean isPrepared;

    private int seekBarPosition;

    private Context context;

    private Timer timer = new Timer();

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (isTracking) {
                return;
            }
            int curPosition = player.getCurrentPosition();
            seekBar.setProgress(curPosition);
        }
    };

    public MyMediaPlayer(Context context, Object dataSource){
        this(context, dataSource, null);
    }

    public MyMediaPlayer(Context context, Object dataSource, SeekBar seekBar) {
        this.context = context;
        setDataSource(dataSource);
        player.setOnBufferingUpdateListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
        initSeekBar(seekBar);
    }

    /**
     * 设置多媒体资源
     * @param dataSource 多媒体资源
     */
    public void setDataSource(Object dataSource){
        if (player != null){
            player.reset();
        }
        if (dataSource instanceof  String){
            player = new MediaPlayer();
            try {
                player.setDataSource(dataSource.toString());
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            player =  MediaPlayer.create(context, (int) dataSource);
        }
    }

    /**
     * 初始化进度条信息
     *
     * @param seekBar 进度条
     */
    private void initSeekBar(SeekBar seekBar) {
        if (seekBar != null) {
            this.seekBar = seekBar;
            seekBar.setMax(player.getDuration());
            timer.schedule(timerTask, 0, 1000);
            seekBar.setOnSeekBarChangeListener(this);
        }
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public void play() {
        if (isPrepared){
            player.start();
        }
    }

    public void pause(){
        player.pause();
    }

    public void stop(){
        player.stop();
    }

    public void relese() {
        player.release();
        timer.cancel();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarPosition = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTracking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTracking = false;
        player.seekTo(seekBarPosition);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(context, "播放完成！！", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
    }
}