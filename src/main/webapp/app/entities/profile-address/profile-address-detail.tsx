import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './profile-address.reducer';
import { IProfileAddress } from 'app/shared/model/profile-address.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IProfileAddressDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ProfileAddressDetail extends React.Component<IProfileAddressDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { profileAddressEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            ProfileAddress [<b>{profileAddressEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="addressType">Address Type</span>
            </dt>
            <dd>{profileAddressEntity.addressType}</dd>
            <dt>
              <span id="address">Address</span>
            </dt>
            <dd>{profileAddressEntity.address}</dd>
            <dt>
              <span id="isDefault">Is Default</span>
            </dt>
            <dd>{profileAddressEntity.isDefault ? 'true' : 'false'}</dd>
            <dt>
              <span id="longitude">Longitude</span>
            </dt>
            <dd>{profileAddressEntity.longitude}</dd>
            <dt>
              <span id="latitude">Latitude</span>
            </dt>
            <dd>{profileAddressEntity.latitude}</dd>
            <dt>User Profile</dt>
            <dd>{profileAddressEntity.userProfileId ? profileAddressEntity.userProfileId : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/profile-address" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/profile-address/${profileAddressEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ profileAddress }: IRootState) => ({
  profileAddressEntity: profileAddress.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ProfileAddressDetail);
