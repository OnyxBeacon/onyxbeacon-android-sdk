package com.byoutline.kickmaterial.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.model.Project;
import com.byoutline.kickmaterial.model.ProjectTime;
import com.byoutline.kickmaterial.utils.AplaTransformation;
import com.byoutline.kickmaterial.utils.OrderedSet;
import com.byoutline.secretsauce.utils.ViewUtils;
import com.onyxbeacon.rest.model.Coupon;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.Collection;
import java.util.List;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.CouponViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    public static final String CURRENCY = "$";

    public static final int BIG_ITEM = 0;
    public static final int NORMAL_ITEM = 1;
    public static final int HEADER_ITEM = 2;
    public static final double IMAGE_RATIO = 4 / 3;


    private final OrderedSet<Project> dataset = new OrderedSet<>();
    // Onyx Adapter model
    private final OrderedSet<Coupon> coupons = new OrderedSet<>();
    private final boolean showHeader;
    private final ItemViewTypeProvider itemViewTypeProvider;
    private Context context;
    private ProjectClickListener projectClickListener;
    private CouponClickListener couponClickListener;
    private final int smallItemWidth;
    private final int smallItemHeight;
    private final int bigItemWidth;
    private final int bigItemHeight;

    // Adapter's Constructor
    public ProjectsAdapter(Context context, ProjectClickListener projectClickListener, boolean showHeader, ItemViewTypeProvider itemViewTypeProvider) {
        this.context = context;
        this.projectClickListener = projectClickListener;
        this.showHeader = showHeader;
        this.itemViewTypeProvider = itemViewTypeProvider;
        smallItemHeight = context.getResources().getDimensionPixelSize(R.dimen.project_item_big_photo_height);
        bigItemHeight = context.getResources().getDimensionPixelSize(R.dimen.project_item_big_height);
        smallItemWidth = (int) (smallItemHeight * IMAGE_RATIO);
        bigItemWidth = (int) (bigItemHeight * IMAGE_RATIO);
    }

    public ProjectsAdapter(Context context, CouponClickListener couponClickListener, boolean showHeader, ItemViewTypeProvider itemViewTypeProvider) {
        this.context = context;
        this.couponClickListener = couponClickListener;
        this.showHeader = showHeader;
        this.itemViewTypeProvider = itemViewTypeProvider;
        smallItemHeight = context.getResources().getDimensionPixelSize(R.dimen.project_item_big_photo_height);
        bigItemHeight = context.getResources().getDimensionPixelSize(R.dimen.project_item_big_height);
        smallItemWidth = (int) (smallItemHeight * IMAGE_RATIO);
        bigItemWidth = (int) (bigItemHeight * IMAGE_RATIO);
    }

    public static void setProjectDetailsInfo(TextView gatheredMoneyTv, TextView totalAmountTv, TextView timeLeftValueTv, TextView timeLeftTypeTv, Project project) {
        gatheredMoneyTv.setText(CURRENCY + project.getGatheredAmount());
        totalAmountTv.setText(gatheredMoneyTv.getContext().getString(R.string.pledged_of, project.getTotalAmount()));
        ProjectTime timeLeft = project.getTimeLeft();
        timeLeftValueTv.setText(timeLeft.value);
        timeLeftTypeTv.setText(timeLeft.description);
    }

    public static void setProjectDetailsInfo(TextView title, TextView descTv, TextView gatheredMoneyTv, TextView totalAmountTv, TextView backersTv, TextView timeLeftValueTv, TextView timeLeftTypeTv, Project project) {
        ViewUtils.setText(title, project.getProjectName());
        ViewUtils.setTextForViewOrHideIt(descTv, project.desc);
        backersTv.setText(Integer.toString(project.backers));
        setProjectDetailsInfo(gatheredMoneyTv, totalAmountTv, timeLeftValueTv, timeLeftTypeTv, project);
    }

    public Project getProjectItem(int position) {
        if (showHeader) {
            dataset.get(position - 1);
        }
        return dataset.get(position);
    }

    public Coupon getItem(int position) {
        // TODO: Check to see what is wrong with the header
        if (showHeader) {
            coupons.get(position - 1);
        }
        return coupons.get(position);
    }

    // Create new views. This is invoked by the layout manager.
    @Override
    public ProjectsAdapter.CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view by inflating the row item xml.
        View v = null;
        switch (viewType) {
            case BIG_ITEM:
                // TODO: Create big item
                //v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_grid_item_big, parent, false);
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_grid_coupon_item_normal, parent, false);
                break;

            case NORMAL_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_grid_coupon_item_normal, parent, false);
                break;

            case HEADER_ITEM:
                // TODO: Create header item
                //v = LayoutInflater.from(parent.getContext()).inflate(R.layout.projects_list_header, parent, false);
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_grid_coupon_item_normal, parent, false);
                break;
        }

        // Set the view to the ViewHolder
        //ViewHolder holder = new ViewHolder(v, projectClickListener);
        CouponViewHolder holder = new CouponViewHolder(v, couponClickListener);
        return holder;
    }

    // Replace the contents of a view. This is invoked by the layout manager.
    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position) {
        int type = getItemViewType(position);

        switch (type) {
            case NORMAL_ITEM:
                Coupon coupon = getItem(position);
                if (coupon != null) {
                    Picasso picasso = Picasso.with(context);
                    final RequestCreator picassoBuilder;
                    picassoBuilder = picasso.load(coupon.imageThumb)
                            .resize(smallItemWidth, smallItemHeight)
                            .placeholder(R.drawable.blank_project_small);
                    picassoBuilder.onlyScaleDown()
                            .transform(new AplaTransformation())
                            .centerCrop()
                            .into(holder.couponImage);
                    holder.couponText.setText(coupon.message);
                }
                break;
            case BIG_ITEM:
                /*Project project = getItem(position);

                if (project != null) {
                    if (holder.projectId == project.id) {
                        // This view holder appears to already show correct project.
                        return;
                    } else {
                        holder.projectId = project.id;
                    }
                    ViewUtils.setTextForViewOrHideIt(holder.projectItemBigTitleTv, project.getProjectName());
                    holder.projectItemBigProgressSb.setProgress((int) project.getPercentProgress());

                    Picasso picasso = Picasso.with(context);
                    final RequestCreator picassoBuilder;
                    if (type == BIG_ITEM) {
                        setProjectDetailsInfo(null, holder.projectItemBigDescTv, holder.projectItemBigGatheredMoneyTv, holder.projectItemBigPledgedOfTv, holder.projectItemBigBackersTv, holder.projectItemBigTimeLeft, holder.projectItemBigTimeLeftType, project);
                        picassoBuilder = picasso.load(project.getBigPhotoUrl())
                                .resize(bigItemWidth, bigItemHeight)
                                .placeholder(R.drawable.blank_project_wide);
                    } else {
                        picassoBuilder = picasso.load(project.getPhotoUrl())
                                .resize(smallItemWidth, smallItemHeight)
                                .placeholder(R.drawable.blank_project_small);
                    }
                    picassoBuilder.onlyScaleDown()
                            .transform(new AplaTransformation())
                            .centerCrop()
                            .into(holder.projectItemBigPhotoIv);
                }*/
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return itemViewTypeProvider.getViewType(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //int size = dataset.size();
        int size = coupons.size();
        return size;
    }

    @Override
    public void onClick(View view) {
//        ViewHolder holder = (ViewHolder) view.getTag();
//        if (view.getId() == holder.mNameTextView.getId()) {
//            Toast.makeText(context, holder.mNameTextView.getText(), Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public boolean onLongClick(View view) {
//        ViewHolder holder = (ViewHolder) view.getTag();
//        if (view.getId() == holder.mNameTextView.getId()) {
//            int pos = holder.getPosition();
//            dataset.remove(pos);
//
//            // Call this method to refresh the list and display the "updated" list
//            notifyItemRemoved(pos);
//
//            Toast.makeText(context, "Item " + holder.mNameTextView.getText() + " has been removed from list",
//                    Toast.LENGTH_SHORT).show();
//        }
        return false;
    }

    public void setItems(List<Project> items) {
        synchronized (dataset) {
            if (showHeader) {
                //Fake project to simulate header
                items.add(0, new Project());
            }
            boolean modified = dataset.setItems(items);
            if (modified) {
                notifyDataSetChanged();
            }
        }
    }

    public void setCouponItems(List<Coupon> couponsList) {
        synchronized (coupons) {
            if (showHeader) {
                //Fake project to simulate header
                couponsList.add(0, new Coupon());
            }
            boolean modified = coupons.setItems(couponsList);
            if (modified) {
                notifyDataSetChanged();
            }
        }
    }

    public void addCouponItem(Coupon coupon) {
        synchronized (coupons) {
            boolean modified = coupons.add(coupon);
            if (modified) {
                notifyDataSetChanged();
            }
        }
    }

    public void addItems(Collection<Project> items) {
        synchronized (dataset) {
            boolean modified = dataset.addAll(items);
            if (modified) {
                notifyDataSetChanged();
            }
        }
    }

    public final static class ItemViewTypeProvider {
        private final boolean showHeader;

        public ItemViewTypeProvider(boolean showHeader) {
            this.showHeader = showHeader;
        }

        public int getViewType(int position) {
            if (showHeader) {
                if (position == 0) {
                    return HEADER_ITEM;
                } else if (position % 5 == 3) {
                    return BIG_ITEM;
                } else {
                    return NORMAL_ITEM;
                }
            } else {
                if ((position + 2) % 5 == 4) {
                    return BIG_ITEM;
                } else {
                    return NORMAL_ITEM;
                }
            }
        }
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView couponImage;
        TextView couponText;
        CouponClickListener mListener;

        public CouponViewHolder(View itemView, CouponClickListener listener) {
            super(itemView);
            this.mListener = listener;
            couponText = (TextView) itemView.findViewById(R.id.project_coupon_item_big_title_tv);
            couponImage = (ImageView) itemView.findViewById(R.id.project_item_big_photo_iv);
        }

        @Override
        public void onClick(View v) {

        }
    }

    // Create the ViewHolder class to keep references to your views
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public ProjectClickListener mListener;

        ImageView projectItemBigPhotoIv;
        TextView projectItemBigTitleTv;
        TextView projectItemBigDescTv;
        SeekBar projectItemBigProgressSb;
        TextView projectItemBigGatheredMoneyTv;
        TextView projectItemBigBackersTv;
        TextView projectItemBigTimeLeft;
        TextView projectItemBigPledgedOfTv;
        TextView projectItemBigTimeLeftType;
        View projectItemBigBackersLabel;
        int projectId;

        /**
         * Constructor
         *
         * @param v The container view which holds the elements from the row item xml
         */
        public ViewHolder(View v, ProjectClickListener projectClickListener) {
            super(v);
            this.mListener = projectClickListener;
            projectItemBigPhotoIv = (ImageView) v.findViewById(R.id.project_item_big_photo_iv);
            projectItemBigTitleTv = (TextView) v.findViewById(R.id.project_item_big_title_tv);
            projectItemBigDescTv = (TextView) v.findViewById(R.id.project_item_big_desc_tv);
            projectItemBigProgressSb = (SeekBar) v.findViewById(R.id.project_item_big_progress_sb);
            projectItemBigGatheredMoneyTv = (TextView) v.findViewById(R.id.project_item_big_gathered_money_tv);
            projectItemBigBackersTv = (TextView) v.findViewById(R.id.project_item_big_backers_tv);
            projectItemBigTimeLeft = (TextView) v.findViewById(R.id.project_item_big_days_left);
            projectItemBigPledgedOfTv = (TextView) v.findViewById(R.id.project_item_big_pledged_of_tv);
            projectItemBigTimeLeftType = (TextView) v.findViewById(R.id.project_item_time_left_type_tv);
            projectItemBigBackersLabel = v.findViewById(R.id.project_item_big_backers_label_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null && getItemViewType() != HEADER_ITEM) {
                SharedViews views = new SharedViews(projectItemBigPhotoIv, projectItemBigTitleTv, projectItemBigTitleTv,
                        projectItemBigProgressSb,
                        projectItemBigGatheredMoneyTv, projectItemBigBackersTv, projectItemBigTimeLeft,
                        projectItemBigPledgedOfTv, projectItemBigBackersLabel, projectItemBigTimeLeftType);
                mListener.projectClicked(getAdapterPosition(), views);
            }
        }
    }
}

