require 'test_helper'

class ParkSpaceControllerTest < ActionDispatch::IntegrationTest
  test "should get new" do
    get park_space_new_url
    assert_response :success
  end

  test "should get create" do
    get park_space_create_url
    assert_response :success
  end

  test "should get update" do
    get park_space_update_url
    assert_response :success
  end

  test "should get edit" do
    get park_space_edit_url
    assert_response :success
  end

  test "should get destroy" do
    get park_space_destroy_url
    assert_response :success
  end

  test "should get index" do
    get park_space_index_url
    assert_response :success
  end

  test "should get sow" do
    get park_space_sow_url
    assert_response :success
  end

end
