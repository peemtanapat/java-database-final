package com.project.code.Repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Review;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByStoreIdAndProductId(Long storeId, Long productId);

}
