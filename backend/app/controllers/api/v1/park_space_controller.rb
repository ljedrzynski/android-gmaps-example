class Api::V1::ParkSpaceController < ApplicationController

  def new
  end

  def create
  end

  def update
  end

  def edit
  end

  def destroy
  end

  def index
     ParkingSpace.all.as_json
  end

  def sow
  end
end
