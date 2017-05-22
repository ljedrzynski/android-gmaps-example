require 'test_helper'

class ParkingSpacesControllerTest < ActionDispatch::IntegrationTest
  setup do
    @parking_space = parking_spaces(:one)
  end

  test "should get index" do
    get parking_spaces_url, as: :json
    assert_response :success
  end

  test "should create parking_space" do
    assert_difference('ParkingSpace.count') do
      post parking_spaces_url, params: { parking_space: {  } }, as: :json
    end

    assert_response 201
  end

  test "should show parking_space" do
    get parking_space_url(@parking_space), as: :json
    assert_response :success
  end

  test "should update parking_space" do
    patch parking_space_url(@parking_space), params: { parking_space: {  } }, as: :json
    assert_response 200
  end

  test "should destroy parking_space" do
    assert_difference('ParkingSpace.count', -1) do
      delete parking_space_url(@parking_space), as: :json
    end

    assert_response 204
  end
end
