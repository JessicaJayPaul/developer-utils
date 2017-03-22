import android.content.Context;
import android.media.MediaPlayer;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MyMediaPlayer implements MediaPlayer.OnBufferingUpdateListener, SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    /**
     * 多媒体播放器
     */
    private MediaPlayer player;

    /**
     * 拖动条
     */
    private SeekBar seekBar;

    /**
     * 拖动条是否正在拖动
     */
    private boolean isTracking;

    /**
     * MediaPlayer是否已经prepare好
     */
    private boolean isPrepared;

    /**
     * 当前SeekBar的位置
     */
    private int seekBarPosition;

    /**
     * 上下文
     */
    private Context context;

    /**
     * 定时器，用于定时更新SeekBar
     */
    private Timer timer = new Timer();

    /**
     * 定时任务
     */
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
        this.seekBar = seekBar;
        setDataSource(dataSource);
        player.setOnBufferingUpdateListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
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
            // sd卡或者网络资源url
            player = new MediaPlayer();
            try {
                player.setDataSource(dataSource.toString());
                // 采用异步prepare，防止阻塞线程
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 资源文件地址为id int类型
            player =  MediaPlayer.create(context, (int) dataSource);
        }
    }

    /**
     * 初始化进度条信息
     */
    private void initSeekBar() {
        if (seekBar != null) {
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

    /**
     * 设置循环播放
     */
    public void setLooping(boolean isLooping){
        player.setLooping(isLooping);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (seekBar != null){
            seekBar.setSecondaryProgress((int)(0.01 * percent * seekBar.getMax()));
        }
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
        Toast.makeText(context, "播放完成！！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 返回false指出现异常后，不断重试，返回true指事件就此终结
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(context, "出现异常！！", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        initSeekBar();
    }
}