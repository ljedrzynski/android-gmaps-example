class Api::V1::ParkingSpacesController < ApplicationController
  before_action :set_parking_space, only: [:show, :update]

  # GET /parking_spaces
  def index
    @parking_spaces = ParkingSpace.all

    render json: @parking_spaces
  end

  # GET /parking_spaces/1
  def show
    render json: @parking_space
  end

  # POST /parking_spaces
  def create
    @parking_space = ParkingSpace.new(parking_space_params)

    if @parking_space.save
      render json: @parking_space, status: :created, location: @parking_space
    else
      render json: @parking_space.errors, status: :unprocessable_entity
    end
  end

  # PATCH/PUT /parking_spaces/1
  def update
    if @parking_space.update(parking_space_params)
      render json: @parking_space
    else
      render json: @parking_space.errors, status: :unprocessable_entity
    end
  end

  # # DELETE /parking_spaces/1
  # def destroy
  #   @parking_space.destroy
  # end

  private
  # Use callbacks to share common setup or constraints between actions.
  def set_parking_space
    @parking_space = ParkingSpace.find(params[:id])
  end

  # Only allow a trusted parameter "white list" through.
  def parking_space_params
    params.fetch(:parking_space, {})
  end
end
