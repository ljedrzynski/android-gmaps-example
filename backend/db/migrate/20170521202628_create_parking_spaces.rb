class CreateParkingSpaces < ActiveRecord::Migration[5.1]
  def change
    create_table :parking_spaces do |t|
      t.decimal :latitude, precision: 15, scale: 10
      t.decimal :longitude, precision: 15, scale: 10
      t.boolean :occupied
      t.references :curr_occupier, foreign_key: true
      t.references :last_occupier, foreign_key: true
      t.references :reporter, foreign_key: true

      t.timestamps
    end
  end
end
