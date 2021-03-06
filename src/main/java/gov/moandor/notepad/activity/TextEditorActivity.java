package gov.moandor.notepad.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import gov.moandor.notepad.R;
import gov.moandor.notepad.bean.Article;
import gov.moandor.notepad.fragment.EditTitleDialogFragment;
import gov.moandor.notepad.util.GlobalContext;

public class TextEditorActivity extends AbsActivity {
    private static final int MAX_TITLE_LENGTH = 30;
    public static final String ARTICLE;
    private static final String EDIT_TEXT_DIALOG_FRAGMENT = "edit_title_dialog_fragment";
    
    static {
        String packageName = GlobalContext.getInstance().getPackageName();
        ARTICLE = packageName + ".ARTICLE";
    }
    
    private EditText mContent;
    private Article mArticle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);
        mContent = (EditText) findViewById(R.id.content);
        mArticle = getIntent().getParcelableExtra(ARTICLE);
        if (mArticle != null) {
            mContent.setText(mArticle.text);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            throw new AssertionError();
        }
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (mArticle != null) {
            actionBar.setTitle(mArticle.title);
        } else {
            actionBar.setTitle(R.string.create);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_text_editor, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mArticle == null) {
            menu.findItem(R.id.view_mode).setVisible(false);
        } else {
            menu.findItem(R.id.view_mode).setVisible(true);
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.save:
            save();
            return true;
        case R.id.edit_title:
            EditTitleDialogFragment editTitleDialog = new EditTitleDialogFragment();
            editTitleDialog.setOnEditFinishedListener(new TitleEditFinishedListener());
            if (mArticle != null) {
                Bundle args = new Bundle();
                args.putString(EditTitleDialogFragment.OLD_TITLE, mArticle.title);
                editTitleDialog.setArguments(args);
            }
            editTitleDialog.show(getFragmentManager(), EDIT_TEXT_DIALOG_FRAGMENT);
            return true;
        case R.id.view_mode:
            TextViewerActivity.start(mArticle, this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void finish() {
        save();
        super.finish();
    }
    
    private void save() {
        String content = mContent.getText().toString();
        if (mArticle == null && !TextUtils.isEmpty(content)) {
            mArticle = new Article();
            mArticle.text = content;
            String title = content.trim();
            if (title.length() > MAX_TITLE_LENGTH) {
                title = title.substring(0, MAX_TITLE_LENGTH);
            }
            mArticle.title = title;
            setResult();
        } else if (mArticle != null && !TextUtils.isEmpty(content) && !content.equals(mArticle.text)) {
            mArticle.text = content;
            setResult();
        }
    }
    
    private void setResult() {
        Intent data = new Intent();
        data.putExtra(ARTICLE, mArticle);
        setResult(RESULT_OK, data);
    }
    
    private class TitleEditFinishedListener implements EditTitleDialogFragment.OnEditFinishedListener {
        @Override
        public void onEditFinished(String result) {
            if (!TextUtils.isEmpty(result)) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(result);
                }
                if (mArticle == null) {
                    mArticle = new Article();
                }
                mArticle.title = result;
                setResult();
            }
        }
    }
}
