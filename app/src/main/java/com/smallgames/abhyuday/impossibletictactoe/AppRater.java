package com.smallgames.abhyuday.impossibletictactoe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

class AppRater {

    protected static void showAppRaterDialog(final Context activityContext, final SharedPrefManager sharedPrefManager) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        LayoutInflater layoutInflater = (LayoutInflater) activityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.app_rater_layout,null);
        builder.setView(customView);
        final AlertDialog appRaterDialog = builder.create();

        Button btnRateNow = (Button) customView.findViewById(R.id.btnRateNow);
        Button btnRateLater = (Button) customView.findViewById(R.id.btnRateLater);
        Button btnRateNoThanks = (Button) customView.findViewById(R.id.btnRateNoThanks);
        TableRow trStars = (TableRow) customView.findViewById(R.id.tableRowStars);

        btnRateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activityContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.smallgames.abhyuday.impossibletictactoe")));
                    sharedPrefManager.setShowAppRaterAnyMore(false);
                } catch (Exception e) {
                    Toast.makeText(activityContext, "Failed to launch Playstore", Toast.LENGTH_SHORT).show();
                } finally {
                    appRaterDialog.dismiss();
                }
            }
        });

        trStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activityContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.smallgames.abhyuday.impossibletictactoe")));
                    sharedPrefManager.setShowAppRaterAnyMore(false);
                } catch (Exception e) {
                    Toast.makeText(activityContext, "Failed to launch Playstore", Toast.LENGTH_SHORT).show();
                } finally {
                    appRaterDialog.dismiss();
                }
            }
        });

        btnRateLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // nothing to do, just close the dialog
                } catch (Exception e) {
                    Toast.makeText(activityContext, "Rate Later exception", Toast.LENGTH_SHORT).show();
                } finally {
                    appRaterDialog.dismiss();
                }
            }
        });

        btnRateNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // set show app rater any more to false
                    sharedPrefManager.setShowAppRaterAnyMore(false);
                } catch (Exception e) {
                    Toast.makeText(activityContext, "Rate No Thanks exception", Toast.LENGTH_SHORT).show();
                } finally {
                    appRaterDialog.dismiss();
                }
            }
        });

        appRaterDialog.show();
    }
}
