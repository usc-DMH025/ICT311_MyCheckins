package android.bignerdranch.mycheckins;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CheckinFragment extends Fragment {

    private static final String ARG_CHECKIN_ID = "checkin_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_IMAGE = 2;

    private Checkin mCheckin;
    private MapsActivity mMap;
    private File mImageFile;
    private EditText mTitleField;
    private EditText mPlaceField;
    private EditText mDetailsField;
    private Button mDateButton;
    private ImageButton mImageButton;
    private ImageView mImageView;
    private GoogleApiClient mClient;
    private Button mShareButton;
    private TextView mLocationView;
    private Button mLocationButton;

    public static CheckinFragment newInstance(UUID checkinId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHECKIN_ID, checkinId);

        CheckinFragment fragment = new CheckinFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID checkinId = (UUID) getArguments().getSerializable(ARG_CHECKIN_ID);
        mCheckin = CheckinLab.get(getActivity()).getCheckin(checkinId);
        mImageFile = CheckinLab.get(getActivity()).getImageFile(mCheckin);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        LocationRequest request = LocationRequest.create();
                        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        request.setNumUpdates(1);
                        request.setInterval(0);

                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                mCheckin.setLat(location.getLatitude());
                                mCheckin.setLon(location.getLongitude());
                                Log.i("LOCATION", "Got a fix: " + location);
                                }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })

                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        CheckinLab.get(getActivity())
                .updateCheckin(mCheckin);
    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_checkin, container, false);

        PackageManager packageManager = getActivity().getPackageManager();

        mImageButton = (ImageButton) v.findViewById(R.id.checkin_image_button);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakeImage = mImageFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mImageButton.setEnabled(canTakeImage);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "android.bignerdranch.mycheckins.fileprovider",
                        mImageFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_IMAGE);
            }
        });

        mImageView = (ImageView) v.findViewById(R.id.checkin_image_view);
        updateImageView();

        mTitleField = (EditText) v.findViewById(R.id.checkin_title);
        mTitleField.setText(mCheckin.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCheckin.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        mPlaceField = (EditText) v.findViewById(R.id.checkin_place);
        mPlaceField.setText(mCheckin.getPlace());
        mPlaceField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCheckin.setPlace(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        mDetailsField = (EditText) v.findViewById(R.id.checkin_details);
        mDetailsField.setText(mCheckin.getDetails());
        mDetailsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCheckin.setDetails(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        mDateButton = (Button) v.findViewById(R.id.checkin_date_button);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCheckin.getDate());
                dialog.setTargetFragment(CheckinFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mLocationView = (TextView) v.findViewById(R.id.checkin_location_text);
        String stringLocation = ("Lat: " + mCheckin.getLat() + "," +  "Lon:" + mCheckin.getLon());
        mLocationView.setText(stringLocation);

        mLocationButton = (Button) v.findViewById(R.id.checkin_location_button);
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Need to come back to this
                Uri intentUri = Uri.parse("geo:" + mCheckin.getLat() + "," + mCheckin.getLon());
                Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        mShareButton = (Button) v.findViewById(R.id.checkin_share_button);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Need to come back to this
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCheckinReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.checkin_summary_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_summary));
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCheckin.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_IMAGE) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "android.bignerdranch.mycheckins.fileprovider",
                    mImageFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updateImageView();
        }
    }

    private void updateDate() {
        mDateButton.setText(mCheckin.getDate().toString());
    }

    private void updateImageView() {
        if (mImageFile == null || !mImageFile.exists()) {
            mImageView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mImageFile.getPath(), getActivity());
            mImageView.setImageBitmap(bitmap);
        }
    }

    private String getCheckinReport() {
        String dateFormat = "EEE, dd/MM";
        String dateString = DateFormat.format(dateFormat,
                mCheckin.getDate()).toString();

        String report = getString(R.string.checkin_summary,
                mCheckin.getTitle(), dateString,
                mCheckin.getPlace(), mCheckin.getDetails());

        return report;
    }
}