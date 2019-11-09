package com.example.meetingscheduler.common.util;


import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class FragmentHelper {

    /**
     * This method pops all the fragments which matches with the specified tag from back-stack and
     * replaces the given fragment.
     *
     * @param id    is used for denoting container id
     * @param frgmt is a fragment object
     */
    public static void popBackStackAndReplace(FragmentActivity activity, Fragment frgmt, int id) {
        if (activity != null && !activity.isFinishing()) {
            FragmentManager manager = activity.getSupportFragmentManager();
            while (manager.getBackStackEntryCount() > 0) {
                manager.popBackStackImmediate();
            }
            //manager.popBackStack(null, 0);
            manager.beginTransaction().replace(id, frgmt).commitAllowingStateLoss();
        }
    }

    /**
     * Replaces fragment without adding it to the back stack .
     */
    public static void replaceFragment(FragmentActivity activity, Fragment fragment,
                                       int containerId) {
        if (!activity.isFinishing()) {
            //fragment.setEnterTransition(fadeTransition());
            FragmentManager manager = activity.getSupportFragmentManager();
            manager.beginTransaction().replace(containerId, fragment).commitAllowingStateLoss();

        }
    }

    /**
     * Replaces fragment without adding it to the back stack .
     */
    public static void replaceFragment(FragmentActivity activity, Fragment fragment,
                                       int containerId, String tag) {
        if (!activity.isFinishing()) {
            FragmentManager manager = activity.getSupportFragmentManager();
            manager.beginTransaction().replace(containerId, fragment, tag).commitAllowingStateLoss();
        }
    }


    /**
     * Replaces and adds the fragment to the back stack.
     */
    public static void replaceAndAddFragment(FragmentActivity activity, Fragment fragment, int containerId) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }

    public static void addFragment(FragmentActivity activity, Fragment fragment, int containerId) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left, R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
            transaction.add(containerId, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * adds the fragment to the back stack.
     */
    public static void addFragmentWithoutCustomTransition(FragmentActivity activity, Fragment fragment, int containerId) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.add(containerId, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }


    /**
     * Replaces and adds the fragment to the back stack.
     */
    public static void addFragmentWithoutAnimation(FragmentActivity activity, Fragment fragment, int containerId, String tag) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.add(containerId, fragment);
            transaction.addToBackStack(tag);
            transaction.commitAllowingStateLoss();
        }
    }


    public static void replaceAndAddFragment(FragmentActivity activity, Fragment fragment, int containerId, String tag) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, tag);
            transaction.addToBackStack(tag);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * This removes current fragment and adds a new child fragment.
     */
    public static void replaceChildFragment(Fragment parentFragment, int containerId, Fragment childFragment) {
        if (parentFragment != null && !parentFragment.isDetached())
            parentFragment.getChildFragmentManager().beginTransaction().replace(containerId, childFragment).commitAllowingStateLoss();
    }

    /**
     * This adds current fragment to backstack and child fragment is added to
     * container
     */

    public static void replaceAndAddChildFragment(Fragment parentFragment, final int containerId, Fragment childFragment) {
        if (parentFragment != null && !parentFragment.isDetached()) {
            FragmentManager fragmentManager = parentFragment.getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(containerId, childFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    public static Fragment getCurrentFragment(FragmentActivity activity, int containerId) {
        return activity.getSupportFragmentManager().findFragmentById(containerId);
    }

    public static void popBackStackImmediate(FragmentActivity activity) {
        if (activity != null && !activity.isFinishing()) {
            try {
                activity.getSupportFragmentManager().popBackStackImmediate();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public static void popBackStack(FragmentActivity activity) {
        if (activity != null && !activity.isFinishing()) {
            try {
                activity.getSupportFragmentManager().popBackStack();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeFragment(FragmentActivity activity, int containerId) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        Fragment f = getCurrentFragment(activity, containerId);
        transaction.detach(f);
        transaction.commit();
    }

    public static void showDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment, String tag) {
        if (fragmentManager.findFragmentByTag(tag) == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(dialogFragment, tag);
            transaction.commitAllowingStateLoss();
        }
    }

    public static void refreshFragment(FragmentActivity activity, int containerId) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        Fragment f = getCurrentFragment(activity, containerId);
        transaction.detach(f);
        transaction.attach(f);
        transaction.replace(containerId, f);
        transaction.commit();
    }

    public static int getBackStackEntryCount(FragmentActivity activity) {
        if (activity != null && !activity.isFinishing())
            return activity.getSupportFragmentManager().getBackStackEntryCount();
        return 0;
    }
}
