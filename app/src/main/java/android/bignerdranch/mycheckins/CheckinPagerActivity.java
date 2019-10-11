package android.bignerdranch.mycheckins;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class CheckinPagerActivity extends AppCompatActivity {
    private static final String EXTRA_CHECKIN_ID =
            "com.bignerdranch.android.mycheckins.checkin_id";

    private ViewPager mViewPager;
    private List<Checkin> mCheckins;

    public static Intent newIntent(Context packageContext, UUID checkinId) {
        Intent intent = new Intent(packageContext, CheckinPagerActivity.class);
        intent.putExtra(EXTRA_CHECKIN_ID, checkinId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_pager);

        UUID checkinId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CHECKIN_ID);

        mViewPager = (ViewPager) findViewById(R.id.checkin_view_pager);

        mCheckins = CheckinLab.get(this).getCheckins();
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {

            @Override
            public Fragment getItem(int position) {
                Checkin checkin = mCheckins.get(position);
                return CheckinFragment.newInstance(checkin.getId());
            }

            @Override
            public int getCount() {
                return mCheckins.size();
            }
        });

        for (int i = 0; i < mCheckins.size(); i++) {
            if (mCheckins.get(i).getId().equals(checkinId)) {
                mViewPager.setCurrentItem(i);
            }
        }
    }
}
