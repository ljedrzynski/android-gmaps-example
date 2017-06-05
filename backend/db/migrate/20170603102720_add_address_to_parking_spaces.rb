class AddAddressToParkingSpaces < ActiveRecord::Migration[5.1]
  def change
    add_column :parking_spaces, :address_info, :string
  end
end
