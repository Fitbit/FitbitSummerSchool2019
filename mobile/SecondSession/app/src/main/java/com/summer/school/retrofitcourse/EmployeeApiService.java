package com.summer.school.retrofitcourse;

import com.summer.school.retrofitcourse.model.EmployeeData;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Retrofit API service for actions like:
 * - get all employees
 * - create new employee
 * - delete a user using his id
 */
public interface EmployeeApiService {
    @GET("employees")
    Call<List<EmployeeData>> getUserDetails();

    @POST("create")
    Call<EmployeeData> createUser(@Body EmployeeData employeeData);

    @DELETE("delete/{userId}")
    Call<ResponseBody> deleteEmployeeById(@Path("userId") int userId);
}
