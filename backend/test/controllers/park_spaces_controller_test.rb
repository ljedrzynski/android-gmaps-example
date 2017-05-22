require 'test_helper'

class ParkSpacesControllerTest < ActionDispatch::IntegrationTest
  setup do
    @park_space = park_spaces(:one)
  end

  test "should get index" do
    get park_spaces_url, as: :json
    assert_response :success
  end

  test "should create park_space" do
    assert_difference('ParkSpace.count') do
      post park_spaces_url, params: { park_space: {  } }, as: :json
    end

    assert_response 201
  end

  test "should show park_space" do
    get park_space_url(@park_space), as: :json
    assert_response :success
  end

  test "should update park_space" do
    patch park_space_url(@park_space), params: { park_space: {  } }, as: :json
    assert_response 200
  end

  test "should destroy park_space" do
    assert_difference('ParkSpace.count', -1) do
      delete park_space_url(@park_space), as: :json
    end

    assert_response 204
  end
end
