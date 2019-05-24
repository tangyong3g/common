package com.sny.tangyong.common.graphic.g3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * 抽象类，继承AbstctApplication,完成3D基本功能，资源加载，相机，世界坐标的参考系
 */
public abstract class Base3D extends AbstractApplication implements GestureDetector.GestureListener {

    private static final String TAG = "Base3D";
    //资源管理器
    public AssetManager mAssets;
    //相机
    public PerspectiveCamera mCam;
    //CameraController
    public CameraInputController mInputController;
    //模型Bath
    public ModelBatch mModelBatch;
    //模型
    public Model mAxesModel;
    //模型实例
    public ModelInstance mAxesInstance;
    //是否显示参考系
    public boolean mShowAxes = true;
    //模型数组
    public Array<ModelInstance> mInstances = new Array<ModelInstance>();
    public final Color mBgColor = new Color(0, 0, 0, 1);

    @Override
    public void create() {
        super.create();
        if (mAssets == null) mAssets = new AssetManager();

        mModelBatch = new ModelBatch();

        //设置相机参数
        mCam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mCam.position.set(18f, 12f, 0f);
        mCam.lookAt(0, 0, 0);
        mCam.near = 0.1f;
        mCam.far = 100f;
        mCam.update();

        //初始化参考系
        createAxes();

        Gdx.input.setInputProcessor(mInputController = new CameraInputController(mCam));
    }

    final float GRID_MIN = -20f;
    final float GRID_MAX = 20f;
    final float GRID_STEP = 1f;


    /**
     * 创建世界坐标参考系
     */
    private void createAxes() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.LIGHT_GRAY);

        for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
            builder.line(t, 0, GRID_MIN, t, 0, GRID_MAX);
            builder.line(GRID_MIN, 0, t, GRID_MAX, 0, t);
        }
        builder = modelBuilder.part("axes", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.RED);
        builder.line(0, 0, 0, 100, 0, 0);
        builder.setColor(Color.GREEN);
        builder.line(0, 0, 0, 0, 100, 0);
        builder.setColor(Color.BLUE);
        builder.line(0, 0, 0, 0, 0, 100);
        mAxesModel = modelBuilder.end();
        mAxesInstance = new ModelInstance(mAxesModel);
    }

    /**
     * 子类完成绘制
     *
     * @param batch
     * @param mInstances
     */
    protected abstract void render(final ModelBatch batch, final Array<ModelInstance> mInstances);

    /**
     * 加载资源的标记
     */
    protected boolean mLoading = true;

    /**
     * 所有的资源都中载完成
     */
    protected abstract void onLoaded();


    @Override
    public void render() {
        super.render();

        if (mLoading && mAssets.update()) {
            mLoading = false;
            onLoaded();
        }

        mInputController.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(mBgColor.r, mBgColor.g, mBgColor.b, mBgColor.a);

        render(mInstances);
    }

    public void render(final Array<ModelInstance> instances) {
        mModelBatch.begin(mCam);
        if (mShowAxes) mModelBatch.render(mAxesInstance);
        mModelBatch.end();

        if (instances != null) {
            render(mModelBatch, instances);
        }

    }

    @Override
    public void dispose() {
        super.dispose();
        mModelBatch.dispose();
        mAssets.dispose();
        mAssets = null;
        mAxesModel.dispose();
        mAxesModel = null;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    @Override
    public boolean panStop(float v, float v1, int i, int i1) {
        return false;
    }
}
