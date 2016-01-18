package git.sureshcs50.tumblrclient.adapters;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import git.sureshcs50.tumblrclient.R;
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
                    break;
                case TYPE_PHOTO_POST:
                    view = mInflater.inflate(R.layout.card_photo_post, null);
                    viewHolder.txtName = (TextView) view.findViewById(R.id.txtName);
                    viewHolder.txtTimestamp = (TextView) view.findViewById(R.id.txtTimestamp);
                    viewHolder.imgPhoto = (ImageView) view.findViewById(R.id.imgPhoto);
                    viewHolder.txtContent = (TextView) view.findViewById(R.id.txtContent);
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

        return view;
    }

    public void addItems(List<Post> posts) {
        // filters only text post and display in ListView..
        this.mPosts.addAll(posts);
        removeDuplicatePosts();
        notifyDataSetChanged();
    }

    private void removeDuplicatePosts() {
        Map<Long, Post> map = new HashMap<>();
        for (Post post : mPosts) map.put(post.getId(), post);
        mPosts.clear();
        for (Map.Entry<Long, Post> mapLoop : map.entrySet()) {
            mPosts.add(mapLoop.getValue());
        }
    }

    public static class ViewHolder {
        public TextView txtName, txtTimestamp, txtTitle, txtContent;
        public ImageView imgPhoto;
    }
}
