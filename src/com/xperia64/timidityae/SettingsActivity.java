/*******************************************************************************
 * Copyright (C) 2014 xperia64 <xperiancedapps@gmail.com>
 * 
 * Copyright (C) 1999-2008 Masanao Izumo <iz@onicos.co.jp>
 *     
 * Copyright (C) 1995 Tuukka Toivonen <tt@cgs.fi>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.xperia64.timidityae;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.xperia64.timidityae.FileBrowserDialog.FileBrowserDialogListener;
import com.xperia64.timidityae.SoundfontDialog.SoundfontDialogListener;
import com.xperia64.timidityae.R;

import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
public class SettingsActivity extends AppCompatActivity implements FileBrowserDialogListener, SoundfontDialogListener {
	
	public static SettingsActivity mInstance = null;
	private ArrayList<String> tmpSounds;
	//private int buffSize;
	private boolean needRestart=false;
	private boolean needUpdateSf=false;
	
	private ListPreference themePref;
	private CheckBoxPreference hiddenFold;
	private CheckBoxPreference showVids;
	private Preference defaultFoldPreference;
	private Preference reinstallSoundfont;
	private Preference lolPref;
	private EditTextPreference manHomeFolder;
	// -- needs restart below -- 
	private CheckBoxPreference manTcfg;
	private Preference sfPref;
	private ListPreference resampMode;
	private ListPreference stereoMode;
	private ListPreference bitMode;
	private ListPreference rates;
	private EditTextPreference bufferSize;
	private Preference dataFoldPreference;
	private EditTextPreference manDataFolder;
	//private PreferenceScreen ds;
	private PreferenceScreen tplus;
	// -- needs restart above -- 
	private CheckBoxPreference nativeMidi;
	private CheckBoxPreference keepWav;
	private SharedPreferences prefs;
	private TimidityPrefsFragment pf;
	private float abElevation;
	
	        @SuppressLint("InlinedApi")
			@Override
            protected void onCreate(Bundle savedInstanceState) {
                        
                        mInstance = this;
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR&&Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                        {
                        	this.setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat);
                        }else{
                        	this.setTheme((Globals.theme==1)?android.support.v7.appcompat.R.style.Theme_AppCompat_Light_DarkActionBar:android.support.v7.appcompat.R.style.Theme_AppCompat);
                        }
                        super.onCreate(savedInstanceState);
        	        	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        	        	
                        // Display the fragment as the main content.
                        FragmentManager mFragmentManager = getSupportFragmentManager();
                        FragmentTransaction mFragmentTransaction = mFragmentManager
                                                .beginTransaction();
                         pf = new TimidityPrefsFragment();
                        mFragmentTransaction.replace(android.R.id.content, pf);
                        mFragmentTransaction.commit();
                        abElevation = getSupportActionBar().getElevation();
            }
	        
	       /* protected void onCreate(Bundle savedInstanceState) {
	        	mInstance = this;
	        	// Themes are borked.
	        	// TODO nix Sherlock
	        	if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
	    		   this.setTheme((Globals.theme==1)?android.R.style.Theme_Holo_Light_DarkActionBar:android.R.style.Theme_Holo);
	    	   }
	        	super.onCreate(savedInstanceState);
	        	getActionBar().setDisplayHomeAsUpEnabled(true);
	                addPreferencesFromResource(R.layout.settings);
	                prefs = PreferenceManager
	    	                .getDefaultSharedPreferences(getBaseContext());
	                themePref = (ListPreference) findPreference("fbTheme");
	                hiddenFold = (CheckBoxPreference) findPreference("hiddenSwitch");
	                showVids = (CheckBoxPreference) findPreference("videoSwitch");
	                defaultFoldPreference = findPreference("defFold");
	                reinstallSoundfont = findPreference("reSF");
	                manHomeFolder = (EditTextPreference) findPreference("defaultPath");
	                dataFoldPreference = findPreference("defData");
	                manDataFolder = (EditTextPreference) findPreference("dataDir");
	                manTcfg = (CheckBoxPreference) findPreference("manualConfig");
	                sfPref = findPreference("sfConfig");
	                resampMode = (ListPreference) findPreference("tplusResamp");
	                stereoMode = (ListPreference) findPreference("sdlChanValue");
	                bitMode = (ListPreference) findPreference("tplusBits");
	                rates = (ListPreference) findPreference("tplusRate");
	                bufferSize = (EditTextPreference) findPreference("tplusBuff");
	                //nativeMidi = (CheckBoxPreference) findPreference("nativeMidiSwitch");
	               // ds = (PreferenceScreen) findPreference("dsKey");
	                tplus = (PreferenceScreen) findPreference("tplusKey");
	                nativeMidi = (CheckBoxPreference) findPreference("nativeMidiSwitch");
	                keepWav = (CheckBoxPreference) findPreference("keepPartialWav");
	                sfPref.setEnabled(!manTcfg.isChecked());
	                lolPref = findPreference("lolWrite");

	                hiddenFold.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(Preference arg0,
								Object arg1) {
								Globals.showHiddenFiles=(Boolean)arg1;
							return true;
						}
	                	
	                });
	                showVids.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(Preference arg0,
								Object arg1) {
								Globals.showVideos=(Boolean)arg1;
							return true;
						}
	                	
	                });
	                nativeMidi.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(Preference arg0,
								Object arg1) {
							if(!Globals.onlyNative)
								Globals.nativeMidi=(Boolean)arg1;
							else
								Globals.nativeMidi=true;
							return true;
						}
	                	
	                });
	                keepWav.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(Preference arg0,
								Object arg1) {
								Globals.keepWav=(Boolean)arg1;
							return true;
						}
	                	
	                });
	                defaultFoldPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	                        public boolean onPreferenceClick(Preference preference) {
	                            // dialog code here
	                        	
	                        	new FileBrowserDialog().create(3, null, SettingsActivity.this, SettingsActivity.this, SettingsActivity.this.getLayoutInflater(), true, prefs.getString("defaultPath", Environment.getExternalStorageDirectory().getAbsolutePath()), getResources().getString(R.string.fb_add));
	                            return true;
	                        }
	                    });
	                if(lolPref!=null)
	                lolPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                        @SuppressLint("NewApi")
						public boolean onPreferenceClick(Preference preference) {
                            // dialog code here
                        	List<UriPermission> permissions = getContentResolver().getPersistedUriPermissions();
                			if(!(permissions==null||permissions.isEmpty()))
                			{
                				for(UriPermission p : permissions)
                				{
                					getContentResolver().releasePersistableUriPermission(p.getUri(), Intent.FLAG_GRANT_READ_URI_PERMISSION |
                	    	                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                				}
                			}
                        	Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    					    startActivityForResult(intent, 42);
                            return true;
                        }
                    });
	                reinstallSoundfont.setOnPreferenceClickListener(new OnPreferenceClickListener(){

						@Override
						public boolean onPreferenceClick(Preference arg0) {
							AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
								
								ProgressDialog pd;
								@Override
								protected void onPreExecute() {
									pd = new ProgressDialog(SettingsActivity.this);
									pd.setTitle(getResources().getString(R.string.extract));
									pd.setMessage(getResources().getString(R.string.extract_sum));
									pd.setCancelable(false);
									pd.setIndeterminate(true);
									pd.show();
								}
									
								@Override
								protected Integer doInBackground(Void... arg0) {
									
									return Globals.extract8Rock(SettingsActivity.this);
								}
								
								@Override
								protected void onPostExecute(Integer result) {
									
									if (pd!=null) {
										pd.dismiss();
										if(result!=777)
										{
											Toast.makeText(SettingsActivity.this, "Could not extrct default soundfont", Toast.LENGTH_SHORT).show();
										}else{
											Toast.makeText(SettingsActivity.this,getResources().getString(R.string.extract_def),Toast.LENGTH_LONG).show();
										}
										//b.setEnabled(true);
									}
								}
									
							};
							task.execute((Void[])null);
							return true;
						}
	                	
	                });
	                dataFoldPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	                        public boolean onPreferenceClick(Preference preference) {
	                            // dialog code here
	                        	needRestart=true;
	                        	new FileBrowserDialog().create(4, null, SettingsActivity.this, SettingsActivity.this, SettingsActivity.this.getLayoutInflater(), true, prefs.getString("dataDir", Environment.getExternalStorageDirectory().getAbsolutePath()), getResources().getString(R.string.fb_add));
	                            return true;
	                        }
	                    });
	                
	                try {
						tmpSounds = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString("tplusSoundfonts", ObjectSerializer.serialize(new ArrayList<String>())));
						for(int i = 0; i<tmpSounds.size();i++)
						{
							if(tmpSounds.get(i)==null)
								tmpSounds.remove(i);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
	               
	                rates.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							needRestart=true;
							String stereo = stereoMode.getValue();
							String sixteen = bitMode.getValue();
							boolean sb=(stereo!=null)?stereo.equals("2"):true;
							boolean sxb=(sixteen!=null)?sixteen.equals("16"):true;
							SparseIntArray mmm = Globals.validBuffers(Globals.validRates(sb,sxb),sb,sxb);
							if(mmm!=null)
							{
								
							int minBuff = mmm.get(Integer.parseInt((String) newValue));
							
							int buff = Integer.parseInt(bufferSize.getText());
							if(buff<minBuff)
							{
								prefs.edit().putString("tplusBuff",Integer.toString(minBuff)).commit();
								bufferSize.setText(Integer.toString(minBuff));
								Toast.makeText(SettingsActivity.this, getResources().getString(R.string.invalidbuff), Toast.LENGTH_SHORT).show();
								((BaseAdapter)tplus.getRootAdapter()).notifyDataSetChanged();
								((BaseAdapter)tplus.getRootAdapter()).notifyDataSetInvalidated();
							}
							}
							return true;
						}
	                });
	                if(tmpSounds == null)
	                	tmpSounds = new ArrayList<String>();
	                manTcfg.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(Preference arg0,
								Object arg1) {
							sfPref.setEnabled(!(Boolean)arg1);
							return true;
						}
	                	
	                });
	                sfPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){

						@Override
						public boolean onPreferenceClick(Preference preference) {
							new SoundfontDialog().create(tmpSounds, SettingsActivity.this, SettingsActivity.this, getLayoutInflater(), prefs.getString("defaultPath", 
									Environment.getExternalStorageDirectory().getPath()));
							return true;
						}
	                	
	                });
	                resampMode.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							needRestart=true;
							return true;
						}
	                	
	                });
	                manDataFolder.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
	                	@Override
						public boolean onPreferenceChange(Preference preference, Object newValue) {
	                		needRestart=true;
							return true;
						}
	                });
	               // buffSize = Integer.parseInt(prefs.getString("tplusBuff", "192000"));
	                //System.out.println("Buffsize is: "+buffSize);
	                Globals.updateBuffers(Globals.updateRates());
	                int[] values = Globals.updateRates();
	                if(values!=null)
	                {
	                CharSequence[] hz = new CharSequence[values.length];
	                CharSequence[] hzItems = new CharSequence[values.length];
	                for(int i = 0; i<values.length; i++)
	                {
	                	hz[i]=Integer.toString(values[i])+"Hz";
	                	hzItems[i]=Integer.toString(values[i]);
	                }
	                rates.setEntries(hz);
	                rates.setEntryValues(hzItems);
	                rates.setDefaultValue(Integer.toString(AudioTrack.getNativeOutputSampleRate(AudioTrack.MODE_STREAM)));
	                rates.setValue(prefs.getString("tplusRate",Integer.toString(AudioTrack.getNativeOutputSampleRate(AudioTrack.MODE_STREAM))));
	                }
	                bufferSize.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(
								Preference preference, final Object newValue) {
							needRestart=true;
							String txt = (String)newValue;
							if(txt!=null)
							{
								if(!TextUtils.isEmpty(txt))
								{
									
									String stereo = stereoMode.getValue();
									String sixteen = bitMode.getValue();
									boolean sb=(stereo!=null)?stereo.equals("2"):true;
									boolean sxb=(sixteen!=null)?sixteen.equals("16"):true;
									SparseIntArray mmm = Globals.validBuffers(Globals.validRates(sb,sxb),sb,sxb);
									if(mmm!=null)
									{
										
									int minBuff = mmm.get(Integer.parseInt(rates.getValue()));
									
									int buff = Integer.parseInt(txt);
									if(buff<minBuff)
									{
										prefs.edit().putString("tplusBuff",Integer.toString(minBuff)).commit();
										((EditTextPreference)preference).setText(Integer.toString(minBuff));
										Toast.makeText(SettingsActivity.this, getResources().getString(R.string.invalidbuff), Toast.LENGTH_SHORT).show();
										((BaseAdapter)tplus.getRootAdapter()).notifyDataSetChanged();
										((BaseAdapter)tplus.getRootAdapter()).notifyDataSetInvalidated();
										return false;
									}
									}
									return true;
									//System.out.println("Text is change");
									//return Globals.updateBuffers(Globals.updateRates());
								}
							}
							return false;
						}
						
	                	
	                });
	                stereoMode.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							needRestart=true;
							String stereo = (String) newValue;
							String sixteen = bitMode.getValue();
							boolean sb=(stereo!=null)?stereo.equals("2"):true;
							boolean sxb=(sixteen!=null)?sixteen.equals("16"):true;
							SparseIntArray mmm = Globals.validBuffers(Globals.validRates(sb,sxb),sb,sxb);
							if(mmm!=null)
							{
								
							int minBuff = mmm.get(Integer.parseInt(rates.getValue()));
							
							int buff = Integer.parseInt(bufferSize.getText());
							if(buff<minBuff)
							{
								prefs.edit().putString("tplusBuff",Integer.toString(minBuff)).commit();
								bufferSize.setText(Integer.toString(minBuff));
								Toast.makeText(SettingsActivity.this, getResources().getString(R.string.invalidbuff), Toast.LENGTH_SHORT).show();
								((BaseAdapter)tplus.getRootAdapter()).notifyDataSetChanged();
								((BaseAdapter)tplus.getRootAdapter()).notifyDataSetInvalidated();
							}
							}
							return true;
						}
	                });
	                bitMode.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							needRestart=true;
							String stereo = stereoMode.getValue();
							String sixteen = (String) newValue;
							boolean sb=(stereo!=null)?stereo.equals("2"):true;
							boolean sxb=(sixteen!=null)?sixteen.equals("16"):true;
							SparseIntArray mmm = Globals.validBuffers(Globals.validRates(sb,sxb),sb,sxb);
							if(mmm!=null)
							{
								
							int minBuff = mmm.get(Integer.parseInt(rates.getValue()));
							
							int buff = Integer.parseInt(bufferSize.getText());
							if(buff<minBuff)
							{
								prefs.edit().putString("tplusBuff",Integer.toString(minBuff)).commit();
								bufferSize.setText(Integer.toString(minBuff));
								Toast.makeText(SettingsActivity.this, getResources().getString(R.string.invalidbuff), Toast.LENGTH_SHORT).show();
								((BaseAdapter)tplus.getRootAdapter()).notifyDataSetChanged();
								((BaseAdapter)tplus.getRootAdapter()).notifyDataSetInvalidated();
							}
							}
							return true;
						}
	                });
	                themePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							Globals.theme=Integer.parseInt((String) newValue);
							Intent intent = getIntent();
				            finish();
				            startActivity(intent);							
				            return true;
						}
	                	
	                });
	                
	        }*/
	        

	        
			/*public static void initializeActionBar(PreferenceScreen preferenceScreen, SettingsActivity s) {
	            final Dialog dialog = preferenceScreen.getDialog();

	            if (dialog != null) {
	            	s.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	                // Inialize the action bar
	                //dialog.getActionBar().setDisplayHomeAsUpEnabled(true);

	                // Apply custom home button area click listener to close the PreferenceScreen because PreferenceScreens are dialogs which swallow
	                // events instead of passing to the activity
	                // Related Issue: https://code.google.com/p/android/issues/detail?id=4611
	                View homeBtn = dialog.findViewById(android.R.id.home);

	                if (homeBtn != null) {
	                    OnClickListener dismissDialogClickListener = new OnClickListener() {
	                        @Override
	                        public void onClick(View v) {
	                            dialog.dismiss();
	                        }
	                    };

	                    // Prepare yourselves for some hacky programming
	                    ViewParent homeBtnContainer = homeBtn.getParent();

	                    // The home button is an ImageView inside a FrameLayout
	                    if (homeBtnContainer instanceof FrameLayout) {
	                        ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();

	                        if (containerParent instanceof LinearLayout) {
	                            // This view also contains the title text, set the whole view as clickable
	                            ((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
	                        } else {
	                            // Just set it on the home button
	                            ((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
	                        }
	                    } else {
	                        // The 'If all else fails' default case
	                        homeBtn.setOnClickListener(dismissDialogClickListener);
	                    }
	                }    
	            }
	        }*/
	        @SuppressLint("NewApi")
			public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
	            final Dialog dialog = preferenceScreen.getDialog();
	            
	            Toolbar bar;

	            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
	            {
	            	LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
	                bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
	                bar.setElevation(abElevation);
	                root.addView(bar,0);
	            }
	            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
	                LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
	                bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
	                root.addView(bar, 0); // insert at top
	            } else {
	                ViewGroup root = (ViewGroup) dialog.findViewById(android.R.id.content);
	                ListView content = (ListView) root.getChildAt(0);

	                root.removeAllViews();

	                bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);

	                int height;
	                TypedValue tv = new TypedValue();
	                if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
	                    height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
	                }else{
	                    height = bar.getHeight();
	                }
	                
	                content.setPadding(0, height, 0, 0);

	                root.addView(content);
	                root.addView(bar);
	            }

	            bar.setTitle(preferenceScreen.getTitle());

	            bar.setNavigationOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    dialog.dismiss();
	                }
	            });
	        }
	        @Override
	        public boolean onOptionsItemSelected(MenuItem item) {
	            if (item.getItemId() == android.R.id.home) {
	                onBackPressed();
	                return true;
	            }
	            return false;
	        }
			@Override
	    	public void onBackPressed() {
	    	    	// Store the soundfonts
	    	    	try {
						prefs.edit().putString("tplusSoundfonts", ObjectSerializer.serialize(tmpSounds)).commit();
					} catch (IOException e) {
						e.printStackTrace();
					}
	    	    	if(needUpdateSf)
	    	    	{
	    	    		Globals.writeCfg(SettingsActivity.this,Globals.dataFolder+"/timidity/timidity.cfg", tmpSounds); // TODO ??
	    	    	}
	    	    		
					Globals.reloadSettings(this, this.getAssets());
					if(needRestart)
	    	    	{	
	    	    		Intent new_intent = new Intent();
	    			    new_intent.setAction(getResources().getString(R.string.msrv_rec));
	    			    new_intent.putExtra(getResources().getString(R.string.msrv_cmd), 18);
	    			    sendBroadcast(new_intent);
	    	    	}
					Intent returnIntent = new Intent();
					setResult(3, returnIntent);
    	    		this.finish();


	    	}

			
			@Override
			public void setItem(String path, int type) {
				if(path!=null)
				{
					if(!TextUtils.isEmpty(path))
					{
						switch(type)
						{
							case 3:
								prefs.edit().putString("defaultPath", path).commit();
								manHomeFolder.setText(path);
								Globals.defaultFolder=path;
								((BaseAdapter)pf.getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
							break;
							case 4:
								prefs.edit().putString("dataDir", path).commit();
								manDataFolder.setText(path);
								((BaseAdapter)pf.getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
							break;
							case 5:
								//soundfont fun
							break;
						}
						return;
					}
				}	
					Toast.makeText(this, getResources().getString(R.string.invalidfold), Toast.LENGTH_SHORT).show();
			}
			@SuppressLint("NewApi")
			@Override
			protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			    // Check which request we're responding to
			    if(requestCode==42)
			    {
			    	 if (resultCode == RESULT_OK) {
			    	        Uri treeUri = data.getData();
			    	        getContentResolver().takePersistableUriPermission(treeUri,
			    	                Intent.FLAG_GRANT_READ_URI_PERMISSION |
			    	                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);  
			    	        Globals.theFold = treeUri;
			    	    }else{
			    	    	Globals.theFold = null;
			    	    }
			    	 
			    }
			}
			@Override
			public void write() {}

			@Override
			public void ignore() {}

			@Override
			public void writeSoundfonts(ArrayList<String> l) {
				needRestart=true;
				needUpdateSf=true;
				tmpSounds = new ArrayList<String>();

				for (String foo : l) {
				  tmpSounds.add(foo);
				}
			}
			public static class TimidityPrefsFragment extends PreferenceFragment {
	        	 
				SettingsActivity s;
                @SuppressWarnings("unchecked")
				@Override
                public void onCreate(Bundle savedInstanceState) {
                			
                            super.onCreate(savedInstanceState);
                            s = (SettingsActivity) getActivity();
                            // Load the preferences from an XML resource
                            addPreferencesFromResource(R.xml.settings);
                            s.prefs = PreferenceManager
        	    	                .getDefaultSharedPreferences(s.getBaseContext());
        	                s.themePref = (ListPreference) findPreference("fbTheme");
        	                s.hiddenFold = (CheckBoxPreference) findPreference("hiddenSwitch");
        	                s.showVids = (CheckBoxPreference) findPreference("videoSwitch");
        	                s.defaultFoldPreference = findPreference("defFold");
        	                s.reinstallSoundfont = findPreference("reSF");
        	                s.manHomeFolder = (EditTextPreference) findPreference("defaultPath");
        	                s.dataFoldPreference = findPreference("defData");
        	                s.manDataFolder = (EditTextPreference) findPreference("dataDir");
        	                s.manTcfg = (CheckBoxPreference) findPreference("manualConfig");
        	                s.sfPref = findPreference("sfConfig");
        	                s.resampMode = (ListPreference) findPreference("tplusResamp");
        	                s.stereoMode = (ListPreference) findPreference("sdlChanValue");
        	                s.bitMode = (ListPreference) findPreference("tplusBits");
        	                s.rates = (ListPreference) findPreference("tplusRate");
        	                s.bufferSize = (EditTextPreference) findPreference("tplusBuff");
        	                //nativeMidi = (CheckBoxPreference) findPreference("nativeMidiSwitch");
        	               // ds = (PreferenceScreen) findPreference("dsKey");
        	                s.tplus = (PreferenceScreen) findPreference("tplusKey");
        	                s.nativeMidi = (CheckBoxPreference) findPreference("nativeMidiSwitch");
        	                s.keepWav = (CheckBoxPreference) findPreference("keepPartialWav");
        	                s.sfPref.setEnabled(!s.manTcfg.isChecked());
        	                s.lolPref = findPreference("lolWrite");

        	                s.hiddenFold.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

        						@Override
        						public boolean onPreferenceChange(Preference arg0,
        								Object arg1) {
        								Globals.showHiddenFiles=(Boolean)arg1;
        							return true;
        						}
        	                	
        	                });
        	                s.showVids.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

        						@Override
        						public boolean onPreferenceChange(Preference arg0,
        								Object arg1) {
        								Globals.showVideos=(Boolean)arg1;
        							return true;
        						}
        	                	
        	                });
        	                s.nativeMidi.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

        						@Override
        						public boolean onPreferenceChange(Preference arg0,
        								Object arg1) {
        							if(!Globals.onlyNative)
        								Globals.nativeMidi=(Boolean)arg1;
        							else
        								Globals.nativeMidi=true;
        							return true;
        						}
        	                	
        	                });
        	                s.keepWav.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

        						@Override
        						public boolean onPreferenceChange(Preference arg0,
        								Object arg1) {
        								Globals.keepWav=(Boolean)arg1;
        							return true;
        						}
        	                	
        	                });
        	                s.defaultFoldPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	                        public boolean onPreferenceClick(Preference preference) {
        	                            // dialog code here
        	                        	
        	                        	new FileBrowserDialog().create(3, null, s, s, s.getLayoutInflater(), true, s.prefs.getString("defaultPath", Environment.getExternalStorageDirectory().getAbsolutePath()), getResources().getString(R.string.fb_add));
        	                            return true;
        	                        }
        	                    });
        	                if(s.lolPref!=null)
        	                	s.lolPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                                @SuppressLint("NewApi")
        						public boolean onPreferenceClick(Preference preference) {
                                    // dialog code here
                                	List<UriPermission> permissions = s.getContentResolver().getPersistedUriPermissions();
                        			if(!(permissions==null||permissions.isEmpty()))
                        			{
                        				for(UriPermission p : permissions)
                        				{
                        					s.getContentResolver().releasePersistableUriPermission(p.getUri(), Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        	    	                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        				}
                        			}
                                	Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            					    startActivityForResult(intent, 42);
                                    return true;
                                }
                            });
        	                s.reinstallSoundfont.setOnPreferenceClickListener(new OnPreferenceClickListener(){

        						@Override
        						public boolean onPreferenceClick(Preference arg0) {
        							AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        						    dialog.setTitle("Reinstall 8Rock11e.sf2?");
        						    dialog.setMessage("This may take a few minutes.");
        						    dialog.setCancelable(true);
        						    dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
        						        public void onClick(DialogInterface dialog, int buttonId) {
        						        	AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
                								
                								ProgressDialog pd;
                								@Override
                								protected void onPreExecute() {
                									pd = new ProgressDialog(s);
                									pd.setTitle(getResources().getString(R.string.extract));
                									pd.setMessage(getResources().getString(R.string.extract_sum));
                									pd.setCancelable(false);
                									pd.setIndeterminate(true);
                									pd.show();
                								}
                									
                								@Override
                								protected Integer doInBackground(Void... arg0) {
                									
                									return Globals.extract8Rock(s);
                								}
                								
                								@Override
                								protected void onPostExecute(Integer result) {
                									
                									if (pd!=null) {
                										pd.dismiss();
                										if(result!=777)
                										{
                											Toast.makeText(s, "Could not extrct default soundfont", Toast.LENGTH_SHORT).show();
                										}else{
                											Toast.makeText(s,getResources().getString(R.string.extract_def),Toast.LENGTH_LONG).show();
                										}
                										//b.setEnabled(true);
                									}
                								}
                									
                							};
                							task.execute((Void[])null);
        						        }
        						    });
        						    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
        						        public void onClick(DialogInterface dialog, int buttonId) {
        						        	
        						        }
        						    });
        						    dialog.show();
        							return true;
        							
        							
        						}
        	                	
        	                });
        	                s.dataFoldPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	                        public boolean onPreferenceClick(Preference preference) {
        	                            // dialog code here
        	                        	s.needRestart=true;
        	                        	new FileBrowserDialog().create(4, null, s, s, s.getLayoutInflater(), true, s.prefs.getString("dataDir", Environment.getExternalStorageDirectory().getAbsolutePath()), getResources().getString(R.string.fb_add));
        	                            return true;
        	                        }
        	                    });
        	                
        	                try {
        	                	s.tmpSounds = (ArrayList<String>) ObjectSerializer.deserialize(s.prefs.getString("tplusSoundfonts", ObjectSerializer.serialize(new ArrayList<String>())));
        						for(int i = 0; i<s.tmpSounds.size();i++)
        						{
        							if(s.tmpSounds.get(i)==null)
        								s.tmpSounds.remove(i);
        						}
        					} catch (IOException e) {
        						e.printStackTrace();
        					}
        	               
        	                s.rates.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

        						@Override
        						public boolean onPreferenceChange(
        								Preference preference, Object newValue) {
        							s.needRestart=true;
        							String stereo = s.stereoMode.getValue();
        							String sixteen = s.bitMode.getValue();
        							boolean sb=(stereo!=null)?stereo.equals("2"):true;
        							boolean sxb=(sixteen!=null)?sixteen.equals("16"):true;
        							SparseIntArray mmm = Globals.validBuffers(Globals.validRates(sb,sxb),sb,sxb);
        							if(mmm!=null)
        							{
        								
        							int minBuff = mmm.get(Integer.parseInt((String) newValue));
        							
        							int buff = Integer.parseInt(s.bufferSize.getText());
        							if(buff<minBuff)
        							{
        								s.prefs.edit().putString("tplusBuff",Integer.toString(minBuff)).commit();
        								s.bufferSize.setText(Integer.toString(minBuff));
        								Toast.makeText(s, getResources().getString(R.string.invalidbuff), Toast.LENGTH_SHORT).show();
        								((BaseAdapter)s.tplus.getRootAdapter()).notifyDataSetChanged();
        								((BaseAdapter)s.tplus.getRootAdapter()).notifyDataSetInvalidated();
        							}
        							}
        							return true;
        						}
        	                });
        	                if(s.tmpSounds == null)
        	                	s.tmpSounds = new ArrayList<String>();
        	                s. manTcfg.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

        						@Override
        						public boolean onPreferenceChange(Preference arg0,
        								Object arg1) {
        							s.sfPref.setEnabled(!(Boolean)arg1);
        							return true;
        						}
        	                	
        	                });
        	                s.sfPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){

        						@Override
        						public boolean onPreferenceClick(Preference preference) {
        							new SoundfontDialog().create(s.tmpSounds, s, s, s.getLayoutInflater(), s.prefs.getString("defaultPath", 
        									Environment.getExternalStorageDirectory().getPath()));
        							return true;
        						}
        	                	
        	                });
        	                s.resampMode.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

        						@Override
        						public boolean onPreferenceChange(
        								Preference preference, Object newValue) {
        							s.needRestart=true;
        							return true;
        						}
        	                	
        	                });
        	                s.manDataFolder.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
        	                	@Override
        						public boolean onPreferenceChange(Preference preference, Object newValue) {
        	                		s.needRestart=true;
        							return true;
        						}
        	                });
        	               // buffSize = Integer.parseInt(prefs.getString("tplusBuff", "192000"));
        	                //System.out.println("Buffsize is: "+buffSize);
        	                Globals.updateBuffers(Globals.updateRates());
        	                int[] values = Globals.updateRates();
        	                if(values!=null)
        	                {
        	                CharSequence[] hz = new CharSequence[values.length];
        	                CharSequence[] hzItems = new CharSequence[values.length];
        	                for(int i = 0; i<values.length; i++)
        	                {
        	                	hz[i]=Integer.toString(values[i])+"Hz";
        	                	hzItems[i]=Integer.toString(values[i]);
        	                }
        	                s.rates.setEntries(hz);
        	                s.rates.setEntryValues(hzItems);
        	                s.rates.setDefaultValue(Integer.toString(AudioTrack.getNativeOutputSampleRate(AudioTrack.MODE_STREAM)));
        	                s.rates.setValue(s.prefs.getString("tplusRate",Integer.toString(AudioTrack.getNativeOutputSampleRate(AudioTrack.MODE_STREAM))));
        	                }
        	                s.bufferSize.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

        						@Override
        						public boolean onPreferenceChange(
        								Preference preference, final Object newValue) {
        							s.needRestart=true;
        							String txt = (String)newValue;
        							if(txt!=null)
        							{
        								if(!TextUtils.isEmpty(txt))
        								{
        									
        									String stereo = s.stereoMode.getValue();
        									String sixteen = s.bitMode.getValue();
        									boolean sb=(stereo!=null)?stereo.equals("2"):true;
        									boolean sxb=(sixteen!=null)?sixteen.equals("16"):true;
        									SparseIntArray mmm = Globals.validBuffers(Globals.validRates(sb,sxb),sb,sxb);
        									if(mmm!=null)
        									{
        										
        									int minBuff = mmm.get(Integer.parseInt(s.rates.getValue()));
        									
        									int buff = Integer.parseInt(txt);
        									if(buff<minBuff)
        									{
        										s.prefs.edit().putString("tplusBuff",Integer.toString(minBuff)).commit();
        										((EditTextPreference)preference).setText(Integer.toString(minBuff));
        										Toast.makeText(s, getResources().getString(R.string.invalidbuff), Toast.LENGTH_SHORT).show();
        										((BaseAdapter)s.tplus.getRootAdapter()).notifyDataSetChanged();
        										((BaseAdapter)s.tplus.getRootAdapter()).notifyDataSetInvalidated();
        										return false;
        									}
        									}
        									return true;
        									//System.out.println("Text is change");
        									//return Globals.updateBuffers(Globals.updateRates());
        								}
        							}
        							return false;
        						}
        						
        	                	
        	                });
        	                s.stereoMode.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

        						@Override
        						public boolean onPreferenceChange(
        								Preference preference, Object newValue) {
        							s.needRestart=true;
        							String stereo = (String) newValue;
        							String sixteen = s.bitMode.getValue();
        							boolean sb=(stereo!=null)?stereo.equals("2"):true;
        							boolean sxb=(sixteen!=null)?sixteen.equals("16"):true;
        							SparseIntArray mmm = Globals.validBuffers(Globals.validRates(sb,sxb),sb,sxb);
        							if(mmm!=null)
        							{
        								
        							int minBuff = mmm.get(Integer.parseInt(s.rates.getValue()));
        							
        							int buff = Integer.parseInt(s.bufferSize.getText());
        							if(buff<minBuff)
        							{
        								s.prefs.edit().putString("tplusBuff",Integer.toString(minBuff)).commit();
        								s.bufferSize.setText(Integer.toString(minBuff));
        								Toast.makeText(s, getResources().getString(R.string.invalidbuff), Toast.LENGTH_SHORT).show();
        								((BaseAdapter)s.tplus.getRootAdapter()).notifyDataSetChanged();
        								((BaseAdapter)s.tplus.getRootAdapter()).notifyDataSetInvalidated();
        							}
        							}
        							return true;
        						}
        	                });
        	                s.bitMode.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

        						@Override
        						public boolean onPreferenceChange(
        								Preference preference, Object newValue) {
        							s.needRestart=true;
        							String stereo = s.stereoMode.getValue();
        							String sixteen = (String) newValue;
        							boolean sb=(stereo!=null)?stereo.equals("2"):true;
        							boolean sxb=(sixteen!=null)?sixteen.equals("16"):true;
        							SparseIntArray mmm = Globals.validBuffers(Globals.validRates(sb,sxb),sb,sxb);
        							if(mmm!=null)
        							{
        								
        							int minBuff = mmm.get(Integer.parseInt(s.rates.getValue()));
        							
        							int buff = Integer.parseInt(s.bufferSize.getText());
        							if(buff<minBuff)
        							{
        								s.prefs.edit().putString("tplusBuff",Integer.toString(minBuff)).commit();
        								s.bufferSize.setText(Integer.toString(minBuff));
        								Toast.makeText(s, getResources().getString(R.string.invalidbuff), Toast.LENGTH_SHORT).show();
        								((BaseAdapter)s.tplus.getRootAdapter()).notifyDataSetChanged();
        								((BaseAdapter)s.tplus.getRootAdapter()).notifyDataSetInvalidated();
        							}
        							}
        							return true;
        						}
        	                });
        	                s.themePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

        						@Override
        						public boolean onPreferenceChange(
        								Preference preference, Object newValue) {
        							Globals.theme=Integer.parseInt((String) newValue);
        							Intent intent = s.getIntent();
        							s.finish();
        				            startActivity(intent);							
        				            return true;
        						}
        	                	
        	                });
                }
                public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    	            super.onPreferenceTreeClick(preferenceScreen, preference);

    	            // If the user has clicked on a preference screen, set up the action bar
    	            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
    		    		   
    		    	   
    	            if (preference instanceof PreferenceScreen) {
    	            	s.setTheme((Globals.theme==1)?android.support.v7.appcompat.R.style.Theme_AppCompat_Light_DarkActionBar:android.support.v7.appcompat.R.style.Theme_AppCompat);
    	            	s.setUpNestedScreen((PreferenceScreen) preference);
    	            }
    	            }
    	            return false;
    	        }
    }
	       
}
