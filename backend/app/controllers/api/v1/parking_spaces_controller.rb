class Api::V1::ParkingSpacesController < ApplicationController
  before_action :set_parking_space, only: [:show, :update]

  # GET /parking_spaces
  def index
    @parking_spaces = ParkingSpace.near([params[:lat], params[:lng]], params[:rad]).where(:occupied => false)
    # @parking_spaces = ParkingSpace
    render json: @parking_spaces
  end

  # GET /parking_spaces/1
  def show
    render json: @parking_space
  end

  # POST /parking_spaces
  def create
    parking_spaces = ParkingSpace.near([parking_space_params[:latitude], parking_space_params[:longitude]], 0.01)
                         .where(:occupied => true, :curr_occupier_id => parking_space_params[:last_occupier_id])

    if parking_spaces.size == 1
      @parking_space = parking_spaces.first
      @parking_space.occupied = false
      @parking_space.last_occupier_id = @parking_space.curr_occupier_id
      @parking_space.curr_occupier_id = nil
      update and return
    end

    @parking_space = ParkingSpace.new(parking_space_params)

    if @parking_space.save
      render json: @parking_space, status: :created, location: [:api, :v1, @parking_space]
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
    params.require(:parking_space).permit(:latitude, :longitude, :occupied, :reporter_id, :curr_occupier_id, :last_occupier_id)
  end
end
