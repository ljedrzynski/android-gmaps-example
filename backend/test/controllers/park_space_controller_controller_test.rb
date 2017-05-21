require 'test_helper'

class ParkSpaceControllerControllerTest < ActionDispatch::IntegrationTest
  test "should get new" do
    get park_space_controller_new_url
    assert_response :success
  end

  test "should get create" do
    get park_space_controller_create_url
    assert_response :success
  end

  test "should get update" do
    get park_space_controller_update_url
    assert_response :success
  end

  test "should get edit" do
    get park_space_controller_edit_url
    assert_response :success
  end

  test "should get destroy" do
    get park_space_controller_destroy_url
    assert_response :success
  end

  test "should get index" do
    get park_space_controller_index_url
    assert_response :success
  end

  test "should get show" do
    get park_space_controller_show_url
    assert_response :success
  end

end
