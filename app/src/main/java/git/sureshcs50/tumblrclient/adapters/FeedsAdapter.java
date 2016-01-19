package git.sureshcs50.tumblrclient.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import git.sureshcs50.tumblrclient.R;
import git.sureshcs50.tumblrclient.asyncs.DeletePostAsync;
import git.sureshcs50.tumblrclient.asyncs.EditPostAsync;
import git.sureshcs50.tumblrclient.utils.Constants;
import git.sureshcs50.tumblrclient.utils.Utils;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class FeedsAdapter extends BaseAdapter {

    private static final int TYPE_TEXT_POST = 0;
    private static final int TYPE_PHOTO_POST = 1;

    private Context mContext;
    private List<Post> mPosts;
    private LayoutInflater mInflater;

    public FeedsAdapter(Context context, List<Post> posts) {
        this.mContext = context;
        this.mPosts = posts;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPosts.size();
    }

    @Override
    public Post getItem(int position) {
        return mPosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return mPosts.get(position) instanceof TextPost ? TYPE_TEXT_POST : TYPE_PHOTO_POST;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        int viewType = getItemViewType(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            switch (viewType) {
                case TYPE_TEXT_POST:
                    view = mInflater.inflate(R.layout.card_text_post, null);
                    viewHolder.txtName = (TextView) view.findViewById(R.id.txtName);
                    viewHolder.txtTimestamp = (TextView) view.findViewById(R.id.txtTimestamp);
                    viewHolder.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
                    viewHolder.txtContent = (TextView) view.findViewById(R.id.txtContent);
                    viewHolder.btnDelete = (Button) view.findViewById(R.id.btnDelete);
                    viewHolder.btnPublish = (Button) view.findViewById(R.id.btnPublish);
                    break;
                case TYPE_PHOTO_POST:
                    view = mInflater.inflate(R.layout.card_photo_post, null);
                    viewHolder.txtName = (TextView) view.findViewById(R.id.txtName);
                    viewHolder.txtTimestamp = (TextView) view.findViewById(R.id.txtTimestamp);
                    viewHolder.imgPhoto = (ImageView) view.findViewById(R.id.imgPhoto);
                    viewHolder.txtContent = (TextView) view.findViewById(R.id.txtContent);
                    viewHolder.btnDelete = (Button) view.findViewById(R.id.btnDelete);
                    viewHolder.btnPublish = (Button) view.findViewById(R.id.btnPublish);
                    break;
            }
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        switch (viewType) {
            case TYPE_TEXT_POST:
                TextPost post = (TextPost) mPosts.get(position);
                viewHolder.txtName.setText(post.getBlogName());
                viewHolder.txtTimestamp.setText(DateUtils.getRelativeTimeSpanString(1000 * post.getTimestamp()));
                viewHolder.txtTitle.setText(post.getTitle());
                viewHolder.txtContent.setText(Html.fromHtml(post.getBody()));
                break;

            case TYPE_PHOTO_POST:
                PhotoPost photoPost = (PhotoPost) mPosts.get(position);
                viewHolder.txtName.setText(photoPost.getBlogName());
                viewHolder.txtTimestamp.setText(DateUtils.getRelativeTimeSpanString(1000 * photoPost.getTimestamp()));
                ImageLoader imageLoader = ImageLoader.getInstance();
                DisplayImageOptions options = Utils.getDisplayImageOptionsBuilder(R.drawable.stub);
                imageLoader.displayImage(photoPost.getPhotos().get(0).getOriginalSize().getUrl(), viewHolder.imgPhoto, options);
                viewHolder.txtContent.setText(Html.fromHtml(photoPost.getCaption()));
                break;
        }

        final Post post = mPosts.get(position);

        if(post.getState().equalsIgnoreCase(Constants.POST_STATE_PUBLISH)){
            viewHolder.btnPublish.setVisibility(View.GONE);
        }

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeletePostAsync(post.getBlogName()+Constants.BASE_HOST_NAME, post.getId()){
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        if(result.equalsIgnoreCase(Constants.RESULT_SUCCESS)){
                            mPosts.remove(post);
                            notifyDataSetChanged();
                        } else{
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.msg_post_delete_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        });

        viewHolder.btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPost(post, Constants.POST_STATE_PUBLISH);
            }
        });

        return view;
    }

    private void editPost(final Post post, String postState) {
        new EditPostAsync(post, postState){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if(result.equalsIgnoreCase(Constants.RESULT_SUCCESS)){
                    mPosts.remove(post);
                    notifyDataSetChanged();
                } else{
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.msg_post_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void addItems(List<Post> posts) {
        if (posts.size() > 0) {
            this.mPosts.addAll(posts);
            removeDuplicatePosts();
            notifyDataSetChanged();
        }
    }

    private void removeDuplicatePosts() {
        Map<Long, Post> map = new LinkedHashMap<>();
        for (Post post : mPosts) map.put(post.getId(), post);
        mPosts.clear();
        for (Map.Entry<Long, Post> mapLoop : map.entrySet()) {
            mPosts.add(mapLoop.getValue());
        }
    }

    public void insertItem(List<Post> posts) {
        if (posts != null) {
            this.mPosts.addAll(0, posts);
            removeDuplicatePosts();
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder {
        public TextView txtName, txtTimestamp, txtTitle, txtContent;
        public ImageView imgPhoto;
        public Button btnDelete, btnPublish;
    }
}
