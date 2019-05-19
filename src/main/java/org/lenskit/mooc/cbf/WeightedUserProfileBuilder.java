package org.lenskit.mooc.cbf;

import org.lenskit.data.ratings.Rating;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Build a user profile from all positive ratings.
 */
public class WeightedUserProfileBuilder implements UserProfileBuilder {
    /**
     * The tag model, to get item tag vectors.
     */
    private final TFIDFModel model;

    @Inject
    public WeightedUserProfileBuilder(TFIDFModel m) {
        model = m;
    }

    @Override
    public Map<String, Double> makeUserProfile(@Nonnull List<Rating> ratings) {
        // Create a new vector over tags to accumulate the user profile
        Map<String,Double> profile = new HashMap<>();

        // Normalize the user's ratings
        double meanRating = 0;
        for (Rating r: ratings) {
          meanRating += r.getValue();
        }
        meanRating = meanRating / ratings.size();
        // Build the user's weighted profile
        for (Rating r: ratings) {
            for (Map.Entry<String,Double> e : this.model.getItemVector(r.getItemId()).entrySet()) {
                double weight = (r.getValue() - meanRating);
                if (profile.get(e.getKey()) == null) {
                  profile.put(e.getKey(), weight * e.getValue());
                } else {
                profile.put(e.getKey(), profile.get(e.getKey()) + weight * e.getValue());
                }
            }
        }


        // The profile is accumulated, return it.
        return profile;
    }
}
