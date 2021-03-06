package spfworld.spfworld.fragment.find.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.common.Callback;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import ab.util.AbLogUtil;
import spfworld.spfworld.MainActivity;
import spfworld.spfworld.R;
import spfworld.spfworld.activity.LoginActivity;
import spfworld.spfworld.activity.User.AcutActivity;
import spfworld.spfworld.activity.User.FriendActivity;
import spfworld.spfworld.activity.User.MyCollectActivity;
import spfworld.spfworld.activity.User.UserMyTribuneActiivity;
import spfworld.spfworld.activity.User.UserOrderActivity;
import spfworld.spfworld.activity.User.UserUpDateActivity;
import spfworld.spfworld.entity.User.UserData;
import spfworld.spfworld.utils.Contants;
import spfworld.spfworld.utils.SharedHelper;
import spfworld.spfworld.utils.XutilsClass;
import spfworld.spfworld.utils.dialog.MyAlertDialog;
import spfworld.spfworld.widget.percent.view.CircleImageView;

/**
 * Created by Administrator on 2016/8/30.
 */
public class UserFragment extends Fragment {
    ArrayList<UserData.DataBean> userdata = new ArrayList<>();
    private Handler handler;
    RelativeLayout relativeuserdate;
    private ScrollView mScrollView;
    @ViewInject(R.id.refreshScrollView)
    private PullToRefreshScrollView refreshScrollView;
    @ViewInject(R.id.user_register_date)
    private RelativeLayout UserLogin;
    @ViewInject(R.id.tv_over)
    private RelativeLayout TVOVER;
    @ViewInject(R.id.user_register)//昵称
    private TextView userregister;
    @ViewInject(R.id.iv_head)
    private CircleImageView ivhead;//头像
    @ViewInject(R.id.tv_date_user)
    private TextView tvdateuser;
    @ViewInject(R.id.tv_city1)//城市
    private TextView tvcity1;
    @ViewInject(R.id.tv_birth1)//生日
    private TextView tvbirth1;
    @ViewInject(R.id.tv_sign1)//签名
    private TextView tvsign1;
    private SharedHelper sharedHelper;
//    private int userid = 8;
    @ViewInject(R.id.swipelayout)
    private LinearLayout layout;
    private Context context;
    View view;
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_my,container,false);
        x.view().inject(this,view);
        sharedHelper=new SharedHelper(getActivity());
//        String user_key=sharedHelper.ReadData("String","user_key").toString();

        GetStatusLogin();
        refreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        // 设置PullRefreshScrollView下拉加载时的加载提示
        refreshScrollView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        refreshScrollView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新...");
        refreshScrollView.getLoadingLayoutProxy(true, false).setReleaseLabel("松开刷新...");
        refreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new GetDataTask().execute();
            }
        });
        mScrollView = refreshScrollView.getRefreshableView();

        return view;
        }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            refreshScrollView.onRefreshComplete();
            super.onPostExecute(result);
            GetUserData();
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//
    }

    //个人信息网络请求
    public void GetUserData(){
        String Userid=sharedHelper.ReadData("String","Userid").toString();
        XutilsClass.getInstance().getUserData(Userid, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e("===user cler",s);
                Gson gson = new Gson();
                UserData userData = gson.fromJson(s,UserData.class);
                ImageLoader.getInstance().displayImage(Contants.IMAGE_BASE_URL4+userData.getData().getHeadpic(),ivhead);
                if (userData.getData()!=null) {
                    AbLogUtil.i("oye","Head == "+userData.getData().getHeadpic().toString());
                    userregister.setText(userData.getData().getNickname().toString());
                    tvcity1.setText(userData.getData().getCity().toString());
                    tvcity1.setTextColor(Color.BLACK);
                    tvbirth1.setText(userData.getData().getBirthday().toString());
                    tvbirth1.setTextColor(Color.BLACK);
                    tvsign1.setText(userData.getData().getSignature().toString());
                    tvsign1.setTextColor(Color.BLACK);
                }
            }
            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.e("==user over",throwable.toString());
            }
            @Override
            public void onCancelled(CancelledException e) {
            }
            @Override
            public void onFinished() {
            }
        });
    }
    @Event(value={
            R.id.relative_user_date,
            R.id.relative_user_acut,R.id.relative_user_friend,
            R.id.relative_user_memory,
            R.id.relative_user_tribune,
            R.id.relative_user_collect,
            R.id.relative_user_orders},type=View.OnClickListener.class)
    private  void oClick(View v) {
        switch (v.getId()) {

            case R.id.relative_user_date://资料设置
                String user_key = sharedHelper.ReadData("String", "user_key").toString();
                if (user_key == "user_key" || user_key == null) {
                    GetStatusLogin2();
                }else {
                    Intent intent = new Intent(getActivity(),UserUpDateActivity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(),"个人信息", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.relative_user_acut://帐号设置
                 user_key = sharedHelper.ReadData("String", "user_key").toString();
                if (user_key == "user_key" || user_key == null) {
                    GetStatusLogin2();
                }else {
                    Intent intent = new Intent(getActivity(),AcutActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.relative_user_friend://邀请好友
                 user_key = sharedHelper.ReadData("String", "user_key").toString();
                if (user_key == "user_key" || user_key == null) {
                    GetStatusLogin2();
                }else {
                    Intent intent = new Intent(getActivity(),FriendActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.relative_user_memory://清除缓存
                final MyAlertDialog myAlertDialog=new MyAlertDialog(getActivity());
                myAlertDialog.setTitle("提示");
                myAlertDialog.setMessage("是否清除缓存?");
                myAlertDialog.SetCancelButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageLoader.getInstance().clearDiskCache();
                        myAlertDialog.Dismiss();
                    }
                });
                myAlertDialog.SetDetermineButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myAlertDialog.Dismiss();
                    }
                });
                myAlertDialog.Show();
                break;
            case R.id.relative_user_tribune://我的帖子
                user_key = sharedHelper.ReadData("String", "user_key").toString();
                if (user_key == "user_key" || user_key == null) {
                    GetStatusLogin2();
                }else {
                    Intent intent = new Intent(getActivity(),UserMyTribuneActiivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.relative_user_collect://我的收藏
                user_key = sharedHelper.ReadData("String", "user_key").toString();
                if (user_key == "user_key" || user_key == null) {
                    GetStatusLogin2();
                }else {
                    Intent intent = new Intent(getActivity(),MyCollectActivity.class);
                    startActivity(intent);
                }
//                Toast.makeText(getActivity(),"收藏信息", Toast.LENGTH_SHORT).show();
                break;
            case R.id.relative_user_orders://我的订单
                user_key = sharedHelper.ReadData("String", "user_key").toString();
                if (user_key == "user_key" || user_key == null) {
                    GetStatusLogin2();
                }else {
                    Intent intent = new Intent(getActivity(), UserOrderActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }


    //    判断登录状态
    public void GetStatusLogin(){
        String user_key=sharedHelper.ReadData("String","user_key").toString();
        if (user_key=="user_key"||user_key==null){
            userregister.setVisibility(View.VISIBLE);
            TVOVER.setVisibility(View.INVISIBLE);
            //登录
            UserLogin.setOnClickListener(new View.OnClickListener() {
               @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
               }
            });
        }else {
            TVOVER.setVisibility(View.VISIBLE);
            TVOVER.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MyAlertDialog myAlertDialog=new MyAlertDialog(getActivity());
                    myAlertDialog.setTitle("提示");
                    myAlertDialog.setMessage("是否退出?");
                    myAlertDialog.SetCancelButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sharedHelper.cleardata();
                            Intent intent=new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();

                        }
                    });
                    myAlertDialog.SetDetermineButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myAlertDialog.Dismiss();
                        }
                    });

                    myAlertDialog.Show();
                }


            });
            GetUserData();
        }
    }

    public void GetStatusLogin2(){
        final MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity());
        myAlertDialog.setTitle("提示");
        myAlertDialog.setMessage("您还没有登录？");
        myAlertDialog.SetCancelButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

            }
        });
        myAlertDialog.SetDetermineButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAlertDialog.Dismiss();
            }
        });

        myAlertDialog.Show();
    }

}

